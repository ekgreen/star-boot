package com.github.old.dog.star.boot.dictionaries.api.throwbles;

/**
 * Исключение, возникающее при ошибках создания справочников в фабрике.
 *
 * <p>Представляет проблемы на уровне фабрики справочников: ошибки конфигурации,
 * недоступность ресурсов, проблемы инициализации зависимостей и т.д.</p>
 *
 * @author AI Assistant
 * @since 1.0
 */
public class DictionaryFactoryException extends DictionaryMetadataException {

    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message детальное сообщение об ошибке
     */
    public DictionaryFactoryException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause причина исключения
     */
    public DictionaryFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создает новое исключение с указанной причиной.
     *
     * @param cause причина исключения
     */
    public DictionaryFactoryException(Throwable cause) {
        super(cause);
    }
}
