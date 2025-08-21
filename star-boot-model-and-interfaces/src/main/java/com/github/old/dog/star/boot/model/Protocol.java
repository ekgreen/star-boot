package com.github.old.dog.star.boot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents various communication protocols used in integration scenarios.
 * <p>
 * This enum provides a comprehensive collection of protocols commonly used in distributed systems
 * along with their associated URI scheme prefixes and default parameters. It supports building
 * connection strings with appropriate parameters for each protocol type.
 * <p>
 * Examples include HTTP-based protocols, message queuing systems, remote procedure calls,
 * and database connection protocols.
 */
@Getter
@RequiredArgsConstructor
public enum Protocol {
    /**
     * Standard Hypertext Transfer Protocol.
     * Used for unsecured web communications.
     */
    HTTP("http", ConnectionType.REST),

    /**
     * Secured Hypertext Transfer Protocol with TLS/SSL.
     * Used for encrypted web communications.
     */
    HTTPS("https", ConnectionType.REST, true),

    /**
     * Apache Kafka message broker protocol.
     * Used for high-throughput distributed messaging.
     */
    KAFKA("kafka", ConnectionType.MESSAGE_QUEUE),

    /**
     * Secured Apache Kafka protocol with TLS/SSL.
     * Used for encrypted Kafka communications.
     */
    KAFKA_SSL("kafka", ConnectionType.MESSAGE_QUEUE, true),

    /**
     * gRPC protocol based on HTTP/2.
     * Used for high-performance remote procedure calls.
     */
    GRPC("grpc", ConnectionType.RPC),

    /**
     * Secured gRPC protocol with TLS/SSL.
     * Used for encrypted remote procedure calls.
     */
    GRPC_SSL("grpc", ConnectionType.RPC, true),

    /**
     * Advanced Message Queuing Protocol.
     * Used for messaging middleware communications.
     */
    AMQP("amqp", ConnectionType.MESSAGE_QUEUE),

    /**
     * Secured AMQP protocol with TLS/SSL.
     * Used for encrypted messaging middleware communications.
     */
    AMQPS("amqps", ConnectionType.MESSAGE_QUEUE, true),

    /**
     * Java Message Service protocol.
     * Used for Java-based messaging applications.
     */
    JMS("jms", ConnectionType.MESSAGE_QUEUE),

    /**
     * JSON-RPC protocol.
     * Used for lightweight remote procedure calls with JSON.
     */
    JSONRPC("jsonrpc", ConnectionType.RPC),

    /**
     * WebSocket protocol.
     * Used for full-duplex communication channels over TCP.
     */
    WS("ws", ConnectionType.WEBSOCKET),

    /**
     * Secured WebSocket protocol with TLS/SSL.
     * Used for encrypted full-duplex communication channels.
     */
    WSS("wss", ConnectionType.WEBSOCKET, true),

    /**
     * Java Remote Method Invocation protocol.
     * Used for Java-to-Java remote method calls.
     */
    RMI("rmi", ConnectionType.RPC),

    /**
     * Simple Object Access Protocol.
     * Used for XML-based message exchange.
     */
    SOAP("soap", ConnectionType.RPC, ParameterDefaults.SOAP_DEFAULTS),

    /**
     * MongoDB database protocol.
     * Used for NoSQL database connections.
     */
    MONGODB("mongodb", ConnectionType.DATABASE),

    /**
     * Secured MongoDB protocol with TLS/SSL.
     * Used for encrypted NoSQL database connections.
     */
    MONGODB_SSL("mongodb", ConnectionType.DATABASE, true),

    /**
     * Redis database protocol.
     * Used for in-memory data structure store connections.
     */
    REDIS("redis", ConnectionType.DATABASE),

    /**
     * Secured Redis protocol with TLS/SSL.
     * Used for encrypted Redis connections.
     */
    REDIS_SSL("redis", ConnectionType.DATABASE, true),

    /**
     * Java Database Connectivity protocol.
     * Used for relational database connections in Java.
     */
    JDBC("jdbc", ConnectionType.DATABASE),

    /**
     * Server Message Block protocol.
     * Used for shared access to files and resources.
     */
    SMB("smb", ConnectionType.FILE),

    /**
     * File Transfer Protocol.
     * Used for file transfers between client and server.
     */
    FTP("ftp", ConnectionType.FILE),

    /**
     * Secured FTP protocol with TLS/SSL.
     * Used for encrypted file transfers.
     */
    FTPS("ftps", ConnectionType.FILE, true),

    /**
     * MQTT protocol for IoT messaging.
     * Used for lightweight publish/subscribe messaging transport.
     */
    MQTT("mqtt", ConnectionType.MESSAGE_QUEUE),

    /**
     * Secured MQTT protocol with TLS/SSL.
     * Used for encrypted IoT messaging.
     */
    MQTTS("mqtts", ConnectionType.MESSAGE_QUEUE, true);

    private final String scheme;
    private final ConnectionType type;
    private final boolean secure;
    private final Map<String, String> defaultParameters;

    /**
     * Constructor for protocols with no default security and no default parameters.
     *
     * @param scheme The URI scheme for the protocol
     * @param type The connection type category
     */
    Protocol(String scheme, ConnectionType type) {
        this(scheme, type, false, Collections.emptyMap());
    }

    /**
     * Constructor for protocols with security setting but no default parameters.
     *
     * @param scheme The URI scheme for the protocol
     * @param type The connection type category
     * @param secure Whether the protocol uses secure communication (SSL/TLS)
     */
    Protocol(String scheme, ConnectionType type, boolean secure) {
        this(scheme, type, secure, Collections.emptyMap());
    }

    /**
     * Constructor for protocols with default parameters but no default security.
     *
     * @param scheme The URI scheme for the protocol
     * @param type The connection type category
     * @param defaultParameters Default connection parameters for this protocol
     */
    Protocol(String scheme, ConnectionType type, Map<String, String> defaultParameters) {
        this(scheme, type, false, defaultParameters);
    }

    /**
     * Returns the URI prefix for this protocol.
     * Includes the scheme and trailing slashes or colons as appropriate.
     *
     * @return The properly formatted URI prefix
     */
    public String getUriPrefix() {
        return scheme + "://";
    }

    /**
     * Builds a connection string using this protocol, the specified host and port,
     * and optional additional parameters.
     *
     * @param host The hostname or IP address
     * @param port The port number, or null for default port
     * @param path The resource path, or null for root
     * @param params Additional connection parameters, or null for none
     * @return A properly formatted connection string
     */
    public String buildConnectionString(String host, Integer port, String path, Map<String, String> params) {
        StringBuilder connectionString = new StringBuilder(getUriPrefix());
        connectionString.append(host);

        if (port != null) {
            connectionString.append(":").append(port);
        }

        if (path != null && !path.isEmpty()) {
            if (!path.startsWith("/")) {
                connectionString.append("/");
            }
            connectionString.append(path);
        }

        // Combine default parameters with provided ones (provided parameters take precedence)
        Map<String, String> allParams = new HashMap<>(defaultParameters);
        if (params != null) {
            allParams.putAll(params);
        }

        if (!allParams.isEmpty()) {
            connectionString.append("?");
            String paramString = allParams.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));
            connectionString.append(paramString);
        }

        return connectionString.toString();
    }

    /**
     * Simplified version of buildConnectionString that only requires host and port.
     *
     * @param host The hostname or IP address
     * @param port The port number, or null for default port
     * @return A basic connection string without path or parameters
     */
    public String buildConnectionString(String host, Integer port) {
        return buildConnectionString(host, port, null, null);
    }

    /**
     * Simplified version of buildConnectionString that only requires host.
     *
     * @param host The hostname or IP address
     * @return A basic connection string with default port
     */
    public String buildConnectionString(String host) {
        return buildConnectionString(host, null, null, null);
    }

    /**
     * Returns a secure version of this protocol if available.
     *
     * @return An Optional containing the secure version of this protocol, or empty if already secure or no secure version exists
     */
    public Optional<Protocol> getSecureVersion() {
        if (secure) {
            return Optional.of(this); // Already secure
        }

        // Try to find a secure version of this protocol
        return Protocol.findSecureVersion(this);
    }

    /**
     * Finds a secure version of the given protocol if one exists.
     *
     * @param protocol The protocol to find a secure version for
     * @return An Optional containing the secure version, or empty if none exists
     */
    public static Optional<Protocol> findSecureVersion(Protocol protocol) {
        String secureVariantName = protocol.name() + "_SSL";
        try {
            return Optional.of(Protocol.valueOf(secureVariantName));
        } catch (IllegalArgumentException e) {
            // Try common naming patterns for secure protocols
            if (protocol == HTTP) {
                return Optional.of(HTTPS);
            } else if (protocol == WS) {
                return Optional.of(WSS);
            } else if (protocol == FTP) {
                return Optional.of(FTPS);
            } else if (protocol == AMQP) {
                return Optional.of(AMQPS);
            } else if (protocol == MQTT) {
                return Optional.of(MQTTS);
            }
            return Optional.empty();
        }
    }

    /**
     * Represents categories of connection types for protocols.
     */
    public enum ConnectionType {
        /**
         * REST-based HTTP protocols.
         */
        REST,

        /**
         * Remote Procedure Call protocols.
         */
        RPC,

        /**
         * Message queue and event streaming protocols.
         */
        MESSAGE_QUEUE,

        /**
         * Database connection protocols.
         */
        DATABASE,

        /**
         * File transfer and access protocols.
         */
        FILE,

        /**
         * WebSocket-based protocols.
         */
        WEBSOCKET
    }

    /**
     * Contains default parameter maps for various protocols.
     */
    private static class ParameterDefaults {
        public static final Map<String, String> SOAP_DEFAULTS = createMap(map -> {
            map.put("wsdl", "true");
            map.put("style", "document");
            return map;
        });

        private static <K, V> Map<K, V> createMap(Function<Map<K, V>, Map<K, V>> builder) {
            return builder.apply(new HashMap<>());
        }
    }
}
