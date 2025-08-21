package com.github.old.dog.star.boot.transport.http.autoconfigure;

import java.util.Optional;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the HTTP client.
 * This class holds the settings for configuring an HTTP client, including the type of client
 * (e.g., OK, Apache) and timeout configurations for connecting, reading, and writing.
 * <p>
 * The settings are mapped from the configuration prefix "star.transport.http".
 * This class is used to retrieve and manage HTTP transport-related properties.
 */
@Data
@ConfigurationProperties(prefix = "star.transport.http")
public class HttpClientProperties {

    /**
     * Тип HTTP клиента (OK, APACHE, etc.)
     */
    private String type;

    /**
     * Настройки таймаутов для HTTP клиента
     */
    private Timeouts timeouts;

    /**
     * Retrieves the connection timeout value from the configuration.
     * If the timeout is not explicitly set, the provided default value is used.
     *
     * @param defaultValue the default connection timeout value to use if no value is configured
     * @return the connection timeout value, either from the configuration or the provided default
     */
    public int getConnectionTimeout(int defaultValue) {
        return Optional.ofNullable(timeouts)
            .map(Timeouts::getConnection)
            .orElse(defaultValue);
    }

    /**
     * Retrieves the read timeout value from the configuration.
     * If the timeout is not explicitly set, the provided default value is used.
     *
     * @param defaultValue the default read timeout value to use if no value is configured
     * @return the read timeout value, either from the configuration or the provided default
     */
    public int getReadTimeout(int defaultValue) {
        return Optional.ofNullable(timeouts)
            .map(Timeouts::getRead)
            .orElse(defaultValue);
    }

    /**
     * Retrieves the write timeout value from the configuration.
     * If the timeout is not explicitly set, the provided default value is used.
     *
     * @param defaultValue the default write timeout value to use if no value is configured
     * @return the write timeout value, either from the configuration or the provided default
     */
    public int getWriteTimeout(int defaultValue) {
        return Optional.ofNullable(timeouts)
            .map(Timeouts::getWrite)
            .orElse(defaultValue);
    }

    @Data
    public static final class Timeouts {

        /**
         * Таймаут соединения в миллисекундах
         *
         * @defaultValue 3000
         */
        private Integer connection;

        /**
         * Таймаут чтения данных в миллисекундах
         *
         * @defaultValue 3000
         */
        private Integer read;

        /**
         * Таймаут записи данных в миллисекундах
         *
         * @defaultValue 3000
         */
        private Integer write;
    }
}
