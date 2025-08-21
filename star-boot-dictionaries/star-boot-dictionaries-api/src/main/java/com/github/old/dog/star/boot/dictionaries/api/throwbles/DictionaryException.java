package com.github.old.dog.star.boot.dictionaries.api.throwbles;

/**
 * Базовое исключение для всех ошибок работы со справочными объектами.
 *
 * <p>Это корневое исключение в иерархии исключений.
 * Все специфические исключения наследуются от данного класса, что позволяет
 * обрабатывать все ошибки справочников единообразно.</p>
 *
 * <p>Исключение является unchecked (наследуется от RuntimeException),
 * что упрощает использование API справочников и соответствует современным
 * практикам проектирования исключений в Java.</p>
 *
 * <h3>Иерархия исключений:</h3>
 * <pre>
 * DictionaryException
 * ├── DictionaryMetadataException
 * </pre>
 *
 * @author AI Assistant
 * @since 1.0
 */
public class DictionaryException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message детальное сообщение об ошибке
     */
    public DictionaryException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause причина исключения (может быть {@code null})
     */
    public DictionaryException(String message, Throwable cause) {
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
    public DictionaryException(Throwable cause) {
        super(cause);
    }
}
