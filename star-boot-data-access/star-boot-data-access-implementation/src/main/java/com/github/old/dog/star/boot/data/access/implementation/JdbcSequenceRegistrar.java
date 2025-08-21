package com.github.old.dog.star.boot.data.access.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import com.github.old.dog.star.boot.data.access.api.JdbcSequence;
import com.github.old.dog.star.boot.data.access.api.SequenceFactory;
import com.github.old.dog.star.boot.interfaces.Sequence;
import com.github.old.dog.star.boot.reflection.ReflectionTools;

/**
 * Implements {@link SequenceFactory} to manage and register JDBC-based sequences for generating unique
 * identifiers. The class maintains a registry to store sequences, categorized by the identifier type
 * and by their unique sequence keys. Each sequence must be annotated with {@link JdbcSequence} to be registered.
 * <p>
 * This registrar primarily allows:
 * - Registration of sequences categorized by identifier type and sequence name.
 * - Retrieval of sequences for a specific identifier type or a combination of type and sequence key.
 * - Insight into all registered identifier types and checks for available sequences.
 * <p>
 * The class is responsible for:
 * - Validating sequences to ensure that they are annotated with {@code @JdbcSequence}.
 * - Ensuring uniqueness of sequence keys across all registered sequences.
 * - Providing thread-safe access to registered sequences.
 */
@Slf4j
public class JdbcSequenceRegistrar implements SequenceFactory {

    private static final Class<JdbcSequence> MARKER = JdbcSequence.class;

    private final Map<Class<?>, List<String>> registry = new HashMap<>();
    private final Map<String, Sequence<?>> namedSequences = new HashMap<>();

    public JdbcSequenceRegistrar(List<Sequence<?>> sequences) {
        if (Objects.isNull(sequences)) {
            return;
        }

        log.info("Инициализация JdbcSequenceRegistrar с {} sequence компонентами", sequences.size());

        for (Sequence<?> sequence : sequences) {
            this.registerSequence(sequence);
        }
    }

    /**
     * Регистрирует отдельную sequence в реестре.
     */
    private void registerSequence(Sequence<?> sequence) {
        final Class<?> sequenceClass = sequence.getClass();

        if (!sequenceClass.isAnnotationPresent(JdbcSequenceRegistrar.MARKER)) {
            if (log.isDebugEnabled()) {
                log.debug(
                    "Сиквенс не будет зарегистрирован в фабрике jdbc-сиквенсов, так как класс не аннотирован как @{}: {}",
                    JdbcSequenceRegistrar.MARKER, sequence
                );
            }

            return;
        }

        final JdbcSequence jdbcSequence = sequenceClass.getDeclaredAnnotation(JdbcSequenceRegistrar.MARKER);

        // получим название
        String sequenceName = jdbcSequence.key();
        // определяем тип ID, с которым работает sequence
        Class<?> idType = ReflectionTools.getParentGenericTypes(0, sequenceClass, Sequence.class);

        this.append(sequenceName, idType, sequence);
    }

    private void append(String sequenceName, Class<?> idType, Sequence<?> sequence) {
        if (namedSequences.containsKey(sequenceName)) {
            throw new IllegalStateException("найдено два сиквенса с одинаковым ключом: " + sequenceName);
        }

        // Регистрируем по типу ID
        registry
            .computeIfAbsent(idType, _ -> new ArrayList<>())
            .add(sequenceName);

        // Регистрируем по имени
        namedSequences
            .put(sequenceName, sequence);

        if (log.isDebugEnabled()) {
            log.debug(
                "Зарегистрирована sequence '{}' для типа {} (класс: {})",
                sequenceName, idType.getSimpleName(), sequence.getClass().getSimpleName()
            );
        }
    }

    // ============================================================================================================== //

    @Override
    public <ID> Map<String, Sequence<ID>> getSequencesForType(Class<ID> idType) {
        List<String> sequences = registry.getOrDefault(idType, Collections.emptyList());

        Map<String, Sequence<ID>> result = new HashMap<>();

        for (String key : sequences) {
            if (namedSequences.containsKey(key)) {
                // noinspection unchecked
                result.put(key, (Sequence<ID>) namedSequences.get(key));
            }
        }

        return result;
    }

    /**
     * Возвращает sequence по имени.
     */
    @Override
    public <ID> Sequence<ID> getSequenceForTypeAndKey(Class<ID> idType, String sequenceKey) {
        // noinspection unchecked
        return (Sequence<ID>) namedSequences.get(sequenceKey);
    }

    /**
     * Возвращает все зарегистрированные типы ID.
     */
    public Set<Class<?>> getRegisteredTypes() {
        return new HashSet<>(registry.keySet());
    }

    /**
     * Проверяет, есть ли sequence для указанного типа ID.
     */
    public boolean hasSequenceForType(Class<?> idType) {
        return registry.containsKey(idType) && !registry.get(idType).isEmpty();
    }

    // ============================================================================================================== //

}
