package com.github.old.dog.star.boot.cache;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Абстракция для работы с кэшированием данных в Spring приложениях.
 * <p>
 * Предоставляет унифицированный интерфейс для операций кэширования различных типов данных
 * с использованием провайдеров данных. Поддерживает получение отдельных элементов по ключу,
 * массовое получение данных, а также управление жизненным циклом кэша.
 * </p>
 *
 * <p><strong>Типовые параметры:</strong></p>
 * <ul>
 *   <li>{@code T} - тип кэшируемых данных (например, {@code CreditRating})</li>
 *   <li>{@code KEY} - тип ключа для идентификации элементов в кэше (например, {@code String})</li>
 *   <li>{@code PROVIDER} - тип провайдера данных (например, {@code CreditRatingProvider})</li>
 * </ul>
 *
 * <p><strong>Пример использования:</strong></p>
 * <pre>{@code
 * CacheSpringShell<CreditRating, String, CreditRatingProvider> cacheShell = ...;
 *
 * // Получение данных с кэшированием
 * CreditRating rating = cacheShell.get("EXPERT_CODE", expertProvider);
 *
 * // Получение всех данных от провайдера
 * List<CreditRating> ratings = cacheShell.getAll(expertProvider);
 *
 * // Очистка кэша конкретного провайдера
 * cacheShell.evictCacheForProvider(expertProvider);
 * }</pre>
 *
 * @param <T>        тип кэшируемых объектов
 * @param <KEY>      тип ключа для доступа к кэшированным данным
 * @param <PROVIDER> тип провайдера данных
 * @author AI Assistant
 * @since 1.0
 */
public interface CacheShell<T, KEY, PROVIDER> {

    /**
     * Получает объект из кэша по ключу, используя указанный провайдер для загрузки данных при отсутствии в кэше.
     * <p>
     * Если объект отсутствует в кэше, он будет загружен через провайдер и сохранен для последующих обращений.
     * Возвращает {@code null} если данные не найдены или произошла ошибка при загрузке.
     * </p>
     *
     * @param key      ключ для поиска объекта в кэше, не должен быть {@code null}
     * @param provider провайдер данных для загрузки объекта при отсутствии в кэше, не должен быть {@code null}
     * @return найденный объект или {@code null} если объект не найден
     * @throws IllegalArgumentException если {@code key} или {@code provider} равны {@code null}
     * @throws CacheOperationException  если произошла ошибка при работе с кэшем
     */
    @Nullable
    default T get(KEY key, PROVIDER provider) {
        throw new UnsupportedOperationException("Метод get(KEY, PROVIDER) не поддерживается в данной реализации");
    }

    /**
     * Получает все объекты от указанного провайдера с использованием кэширования.
     * <p>
     * Если данные отсутствуют в кэше, они будут загружены через провайдер полностью
     * и сохранены для последующих обращений. Метод всегда возвращает непустой список,
     * даже если данные отсутствуют (возвращается пустой список).
     * </p>
     *
     * @param provider провайдер данных для загрузки объектов при отсутствии в кэше, не должен быть {@code null}
     * @return список всех объектов от провайдера, никогда не {@code null}
     * @throws IllegalArgumentException если {@code provider} равен {@code null}
     * @throws CacheOperationException  если произошла ошибка при работе с кэшем
     * @throws DataProviderException    если произошла ошибка при загрузке данных от провайдера
     */
    @NotNull
    default List<T> getAll(PROVIDER provider) {
        throw new UnsupportedOperationException("Метод getAll(PROVIDER) не поддерживается в данной реализации");
    }

    /**
     * Принудительно очищает кэш для указанного провайдера данных.
     * <p>
     * После вызова этого метода все данные, связанные с указанным провайдером,
     * будут удалены из кэша. При следующем обращении данные будут загружены заново.
     * Операция выполняется синхронно и блокирует выполнение до завершения очистки.
     * </p>
     *
     * @param provider провайдер данных, для которого необходимо очистить кэш, не должен быть {@code null}
     * @throws IllegalArgumentException если {@code provider} равен {@code null}
     * @throws CacheOperationException  если произошла ошибка при очистке кэша
     */
    default void evictCacheForProvider(PROVIDER provider) {
        throw new UnsupportedOperationException("Метод evictCacheForProvider(PROVIDER) не поддерживается в данной реализации");
    }

    /**
     * Принудительно очищает кэш для провайдера по его имени.
     * <p>
     * Этот метод удобен для использования в REST API или административных интерфейсах,
     * где имя провайдера передается как строка. После вызова все данные, связанные
     * с провайдером с указанным именем, будут удалены из кэша.
     * </p>
     *
     * <p><strong>Пример использования:</strong></p>
     * <pre>{@code
     * // Очистка кэша для конкретного провайдера
     * cacheShell.evictCacheForProviderByName("EXPERT_CODE");
     *
     * // Использование в REST контроллере
     * @PostMapping("/cache/evict/{providerName}")
     * public ResponseEntity<String> evictCache(@PathVariable String providerName) {
     *     cacheShell.evictCacheForProviderByName(providerName);
     *     return ResponseEntity.ok("Кэш очищен для провайдера: " + providerName);
     * }
     * }</pre>
     *
     * @param providerName имя провайдера данных, для которого необходимо очистить кэш, не должно быть {@code null}
     * @throws IllegalArgumentException если {@code providerName} равно {@code null}
     * @throws CacheOperationException  если произошла ошибка при очистке кэша
     * @apiNote Предпочтительно использовать {@link #evictCacheForProvider(Object)} если у вас есть ссылка на провайдер
     */
    default void evictCacheForProviderByName(Object providerName) {
        throw new UnsupportedOperationException("Метод evictCacheForProviderByName(Object) не поддерживается в данной реализации");
    }


    /**
     * Принудительно очищает весь кэш, удаляя все кэшированные данные независимо от провайдера.
     * <p>
     * Это радикальная операция, которая полностью очищает кэш от всех данных.
     * Используйте с осторожностью, так как после вызова этого метода все данные
     * придется загружать заново. Операция выполняется синхронно.
     * </p>
     *
     * @throws CacheOperationException если произошла ошибка при полной очистке кэша
     * @apiNote Рекомендуется использовать {@link #evictCacheForProvider(Object)} для точечной очистки
     */
    default void evictAllCache() {
        throw new UnsupportedOperationException("Метод evictAllCache() не поддерживается в данной реализации");
    }

    /**
     * Проверяет наличие данных в кэше для указанного ключа.
     *
     * @param key ключ для проверки
     * @return true если данные есть в кэше, false иначе
     */
    default boolean containsKey(KEY key) {
        throw new UnsupportedOperationException("Метод containsKey(KEY) не поддерживается в данной реализации");
    }


}
