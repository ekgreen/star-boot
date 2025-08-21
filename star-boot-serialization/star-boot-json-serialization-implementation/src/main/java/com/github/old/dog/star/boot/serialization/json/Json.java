package com.github.old.dog.star.boot.serialization.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.old.dog.star.boot.model.DataType;
import com.github.old.dog.star.boot.reflection.type.ClassRefType;
import com.github.old.dog.star.boot.reflection.type.Ref;
import com.github.old.dog.star.boot.reflection.type.TypeRef;
import com.github.old.dog.star.boot.serialization.api.ReaderVisitor;
import com.github.old.dog.star.boot.serialization.json.throwbles.JsonConversionException;
import com.github.old.dog.star.boot.serialization.json.value.ByteArrayJsonValue;
import com.github.old.dog.star.boot.serialization.json.value.InputStreamJsonValue;
import com.github.old.dog.star.boot.serialization.json.value.StringJsonValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;


/**
 * Utility class for JSON serialization and deserialization operations.
 * <p>
 * This class provides methods for converting between Java objects and JSON representations
 * in various formats (String, byte array, JsonNode). It uses Jackson's ObjectMapper for
 * the actual JSON processing and supports different input and output types.
 * <p>
 * The class implements a pattern that allows processing of JSON from different sources
 * (String, byte array, InputStream) in a uniform way using visitor pattern.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Json {

    private static final ObjectMapper DEFAULT_JSON_CONVERTER = Json.getBasicJsonMapper();

    // ===============================================================================================================//

    /**
     * Default Jackson ObjectMapper configured with common settings for JSON processing.
     * <p>
     * Configuration includes:
     * <ul>
     *   <li>Field visibility set to ANY (to access private fields)</li>
     *   <li>Ignoring unknown properties during deserialization</li>
     *   <li>Compact output format (no indentation)</li>
     *   <li>Null values excluded from serialization</li>
     *   <li>Java 8 date/time support via JavaTimeModule</li>
     * </ul>
     */
    public static ObjectMapper getBasicJsonMapper() {
        return new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .enable(JsonGenerator.Feature.IGNORE_UNKNOWN)
            .disable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule());
    }

    // ===============================================================================================================//

    /**
     * Serializes an object to a byte array containing JSON using the default ObjectMapper.
     *
     * @param value the object to be serialized
     * @return the serialized JSON as a byte array
     * @throws JsonConversionException if serialization fails
     */
    public static byte[] writeAsBytes(Object value) {
        return Json.writeAsBytes(value, Json.DEFAULT_JSON_CONVERTER);
    }

    /**
     * Serializes an object to a byte array containing JSON using the provided ObjectMapper.
     *
     * @param value        the object to be serialized
     * @param objectMapper the ObjectMapper to use for serialization
     * @return the serialized JSON as a byte array
     * @throws JsonConversionException if serialization fails
     */
    public static byte[] writeAsBytes(Object value, ObjectMapper objectMapper) {
        return Json.writeAs(DataType.BYTE_ARRAY_TYPE, value, objectMapper);
    }

    /**
     * Serializes an object to a JSON string using the default ObjectMapper.
     *
     * @param value the object to be serialized
     * @return the serialized JSON as a String
     * @throws JsonConversionException if serialization fails
     */
    public static String writeAsString(Object value) {
        return Json.writeAsString(value, Json.DEFAULT_JSON_CONVERTER);
    }

    /**
     * Serializes an object to a JSON string using the provided ObjectMapper.
     *
     * @param <R>          the return type (typically String)
     * @param value        the object to be serialized
     * @param objectMapper the ObjectMapper to use for serialization
     * @return the serialized JSON as a String
     * @throws JsonConversionException if serialization fails
     */
    public static <R> R writeAsString(Object value, ObjectMapper objectMapper) {
        return Json.writeAs(DataType.STRING_TYPE, value, objectMapper);
    }

    /**
     * Converts an object to a Jackson JsonNode using the default ObjectMapper.
     * <p>
     * This method allows for further manipulation of the JSON structure using
     * Jackson's tree model.
     *
     * @param value the object to be converted to a JsonNode
     * @return the object represented as a JsonNode
     * @throws JsonConversionException if conversion fails
     */
    public static JsonNode writeAsTree(Object value) {
        return Json.writeAsTree(value, Json.DEFAULT_JSON_CONVERTER);
    }

    /**
     * Converts an object to a Jackson JsonNode using the provided ObjectMapper.
     * <p>
     * This method allows for further manipulation of the JSON structure using
     * Jackson's tree model.
     *
     * @param value        the object to be converted to a JsonNode
     * @param objectMapper the ObjectMapper to use for conversion
     * @return the object represented as a JsonNode
     * @throws JsonConversionException if conversion fails
     */
    public static JsonNode writeAsTree(Object value, ObjectMapper objectMapper) {
        return Json.writeAs("tree", value, objectMapper);
    }

    /**
     * Core serialization method that handles different output types.
     * <p>
     * This method supports converting an object to different JSON representations:
     * <ul>
     *   <li>"bytes" - converts to a byte array</li>
     *   <li>"string" - converts to a String</li>
     *   <li>"tree" - converts to a JsonNode</li>
     * </ul>
     *
     * @param <R>          the return type, determined by the type parameter
     * @param type         the type of output ("bytes", "string", or "tree")
     * @param value        the object to be serialized
     * @param objectMapper the ObjectMapper to use for serialization
     * @return the serialized JSON in the requested format
     * @throws JsonConversionException  if serialization fails
     * @throws IllegalArgumentException if an unknown type is specified
     */
    public static <R> R writeAs(String type, Object value, ObjectMapper objectMapper) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Начало сериализации объекта в JSON");
            }

            Object result;
            int length = -1;

            switch (type) {
                case DataType.BYTE_ARRAY_TYPE -> {
                    byte[] bytes = objectMapper.writeValueAsBytes(value);

                    result = bytes;
                    length = bytes.length;
                }
                case DataType.SHORT_TYPE -> {
                    String string = objectMapper.writeValueAsString(value);

                    result = string;
                    length = string.getBytes().length;
                }
                case "tree" -> result = objectMapper.valueToTree(value);
                default -> throw new IllegalArgumentException("unknown `type`: " + type);
            }

            if (log.isDebugEnabled()) {
                log.debug("Объект успешно сериализован в JSON, размер: {} байт", length);
            }

            // noinspection unchecked
            return (R) result;
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации объекта в JSON: {}", e.getMessage());
            throw new JsonConversionException("Ошибка при сериализации объекта в JSON: " + e.getMessage(), e);
        }
    }

    // ===============================================================================================================//

    /**
     * Deserializes JSON data to an object of the type specified by the TypeReference
     * using the default ObjectMapper.
     * <p>
     * This method is particularly useful for deserializing to generic types.
     *
     * @param <T>       the input type containing JSON data (String, byte[], or InputStream)
     * @param <R>       the target type to deserialize to
     * @param value     the JSON data to deserialize
     * @param reference the TypeReference specifying the target type
     * @return the deserialized object
     * @throws JsonConversionException if deserialization fails
     */
    public static <T, R> R readAs(T value, TypeReference<R> reference) {
        return Json.readAs(value, Json.DEFAULT_JSON_CONVERTER, reference);
    }

    /**
     * Deserializes JSON data to an object of the specified class
     * using the default ObjectMapper.
     *
     * @param <T>   the input type containing JSON data (String, byte[], or InputStream)
     * @param <R>   the target type to deserialize to
     * @param value the JSON data to deserialize
     * @param clazz the Class object specifying the target type
     * @return the deserialized object
     * @throws JsonConversionException if deserialization fails
     */
    public static <T, R> R readAs(T value, Class<R> clazz) {
        return Json.readAs(value, Json.DEFAULT_JSON_CONVERTER, clazz);
    }

    /**
     * Deserializes JSON data to an object of the specified class
     * using the provided ObjectMapper.
     *
     * @param <T>          the input type containing JSON data (String, byte[], or InputStream)
     * @param <R>          the target type to deserialize to
     * @param value        the JSON data to deserialize
     * @param objectMapper the ObjectMapper to use for deserialization
     * @param reference    the Class object specifying the target type
     * @return the deserialized object
     * @throws JsonConversionException if deserialization fails
     */
    public static <T, R> R readAs(T value, ObjectMapper objectMapper, TypeReference<R> reference) {
        return Json.readAs(value, new TypeRef<>(reference), new ReaderVisitor<>() {
                @Override
                public R visitString(String str) throws Exception {
                    return objectMapper.readValue(str, reference);
                }

                @Override
                public R visitBytes(byte[] bytes) throws Exception {
                    return objectMapper.readValue(bytes, reference);
                }

                @Override
                public R visitInputStream(InputStream is) throws Exception {
                    return objectMapper.readValue(is, reference);
                }
            }
        );
    }

    /**
     * Deserializes JSON data to an object of the specified class
     * using the provided ObjectMapper.
     *
     * @param <T>          the input type containing JSON data (String, byte[], or InputStream)
     * @param <R>          the target type to deserialize to
     * @param value        the JSON data to deserialize
     * @param objectMapper the ObjectMapper to use for deserialization
     * @param clazz        the Class object specifying the target type
     * @return the deserialized object
     * @throws JsonConversionException if deserialization fails
     */
    public static <T, R> R readAs(T value, ObjectMapper objectMapper, Class<R> clazz) {
        return Json.readAs(value, new ClassRefType<>(clazz), new ReaderVisitor<>() {
                @Override
                public R visitString(String str) throws Exception {
                    return objectMapper.readValue(str, clazz);
                }

                @Override
                public R visitBytes(byte[] bytes) throws Exception {
                    return objectMapper.readValue(bytes, clazz);
                }

                @Override
                public R visitInputStream(InputStream is) throws Exception {
                    return objectMapper.readValue(is, clazz);
                }
            }
        );
    }

    /**
     * Core deserialization method that processes JSON data using the visitor pattern.
     * <p>
     * This method handles the common logic for all deserialization operations, including
     * error handling and logging. It determines the appropriate visitor method to call
     * based on the type of the input value.
     *
     * @param <T>       the input type containing JSON data (String, byte[], or InputStream)
     * @param <R>       the target type to deserialize to
     * @param value     the JSON data to deserialize
     * @param reference a reference object providing type information for logging
     * @param visitor   the visitor that will process the JSON data
     * @return the deserialized object
     * @throws JsonConversionException if deserialization fails
     */
    public static <T, R> R readAs(T value, Ref reference, ReaderVisitor<R> visitor) {
        try {
            if (log.isDebugEnabled()) {
                log.debug(
                    "Начало конвертации в объект класса {}. Тип входных данных: {}",
                    reference.getRefType(),
                    value != null ? value.getClass().getSimpleName() : "null"
                );
            }

            if (value == null) {
                log.error("Получено null значение для конвертации");
                throw new JsonConversionException("Значение для конвертации не может быть null", null);
            }

            R result = switch (value) {
                case String str -> {
                    if (log.isDebugEnabled()) {
                        log.debug("Конвертация из строки длиной: {}", str.length());
                    }

                    yield new StringJsonValue(str).read(visitor);
                }
                case byte[] bytes -> {
                    if (log.isDebugEnabled()) {
                        log.debug("Конвертация из массива байт размером: {}", bytes.length);
                    }
                    yield new ByteArrayJsonValue(bytes).read(visitor);
                }
                case InputStream is -> {
                    if (log.isDebugEnabled()) {
                        log.debug("Конвертация из InputStream");
                    }
                    yield new InputStreamJsonValue(is).read(visitor);
                }
                default -> {
                    log.error("Неподдерживаемый тип входных данных: {}", value.getClass().getName());
                    throw new JsonConversionException(
                        "Неподдерживаемый тип входных данных: " + value.getClass().getName(),
                        null
                    );
                }
            };

            if (log.isDebugEnabled()) {
                log.debug(
                    "JSON успешно преобразован в объект класса {}. Результат: {}",
                    reference.getRefType(),
                    result != null ? "не null" : "null"
                );
            }
            return result;

        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Ошибка при разборе JSON в класс {}: {}", reference.getRefType(), jsonProcessingException.getMessage());
            throw new JsonConversionException(
                "Ошибка при преобразовании JSON в " + reference.getRefType() + ": " + jsonProcessingException.getMessage(),
                jsonProcessingException
            );
        } catch (IOException ioException) {
            log.error("Ошибка ввода-вывода при конвертации в класс {}: {}", reference.getRefType(), ioException.getMessage());
            throw new JsonConversionException(
                "Ошибка ввода-вывода при конвертации в " + reference.getRefType() + ": " + ioException.getMessage(),
                ioException
            );
        } catch (Exception unexpectedException) {
            log.error("Непредвиденная ошибка при конвертации в класс {}: {}", reference.getRefType(), unexpectedException.getMessage());
            throw new JsonConversionException(
                "Непредвиденная ошибка при конвертации в " + reference.getRefType() + ": " + unexpectedException.getMessage(),
                unexpectedException
            );
        }
    }
}
