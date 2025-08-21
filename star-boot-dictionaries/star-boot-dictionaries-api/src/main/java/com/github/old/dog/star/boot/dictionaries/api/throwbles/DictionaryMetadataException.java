package com.github.old.dog.star.boot.dictionaries.api.throwbles;

import com.github.old.dog.star.boot.dictionaries.api.DictionaryMetadata;

/**
 * Базовое исключение для всех ошибок работы с метаданными словарных объектов.
 *
 * <p>Это корневое исключение в иерархии исключений метаданных справочников.
 * Все специфические исключения наследуются от данного класса, что позволяет
 * обрабатывать все ошибки метаданных единообразно.</p>
 *
 * <p>Исключение является unchecked (наследуется от RuntimeException),
 * что упрощает использование API метаданных и соответствует современным
 * практикам проектирования исключений в Java.</p>
 *
 * <h3>Иерархия исключений:</h3>
 * <pre>
 * DictionaryMetadataException
 * ├── DictionaryInstantiationException
 * ├── DictionaryAttributeNotFoundException
 * ├── DictionaryTypeConversionException
 * └── DictionaryAccessException
 * </pre>
 *
 * @author AI Assistant
 * @since 1.0
 * @see DictionaryMetadata
 */
public class DictionaryMetadataException extends DictionaryException {

    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message детальное сообщение об ошибке
     */
    public DictionaryMetadataException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause причина исключения (может быть {@code null})
     */
    public DictionaryMetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создает новое исключение с указанной причиной.
     *
     * <p>Сообщение об ошибке будет автоматически сформировано на основе
     * строкового представления причины.</p>
     *
     * @param cause причина исключения (может быть {@code null})
     */
    public DictionaryMetadataException(Throwable cause) {
        super(cause);
    }
}
