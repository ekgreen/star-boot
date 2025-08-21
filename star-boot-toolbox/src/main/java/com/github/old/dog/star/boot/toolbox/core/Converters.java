package com.github.old.dog.star.boot.toolbox.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.old.dog.star.boot.interfaces.Converter;
import com.github.old.dog.star.boot.serialization.json.Json;
import com.github.old.dog.star.boot.toolbox.documents.core.xslx.Xslx;
import com.github.old.dog.star.boot.toolbox.documents.core.xslx.XslxRowMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * Utility class providing various converter functions for data transformation operations.
 * This class includes methods for JSON conversions, string encoding/decoding, and other
 * common data transformation tasks.
 * <p>
 * All methods are static and the class cannot be instantiated.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Converters {

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
    private static final ObjectMapper DEFAULT_JSON_CONVERTER = new ObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .enable(JsonGenerator.Feature.IGNORE_UNKNOWN)
        .disable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModule(new JavaTimeModule());

    /**
     * Creates a converter that transforms byte arrays to strings using UTF-8 encoding.
     *
     * @return a converter from byte[] to String using UTF-8 charset
     */
    public static Converter<byte[], String> utf8() {
        return bytes -> new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Creates a converter that transforms input to the specified class type using
     * the default JSON ObjectMapper.
     *
     * @param <T>       the source type
     * @param <R>       the target type
     * @param reference the class reference for the target type
     * @return a converter from T to R using JSON deserialization
     */
    public static <T, R> Converter<T, R> jsonReaderAs(Class<R> reference) {
        return Converters.jsonReaderAs(Converters.DEFAULT_JSON_CONVERTER, reference);
    }

    /**
     * Creates a converter that transforms input to the specified class type using
     * the provided ObjectMapper.
     *
     * @param <T>          the source type
     * @param <R>          the target type
     * @param objectMapper the ObjectMapper to use for deserialization
     * @param reference    the class reference for the target type
     * @return a converter from T to R using JSON deserialization
     */
    public static <T, R> Converter<T, R> jsonReaderAs(ObjectMapper objectMapper, Class<R> reference) {
        return value -> Json.readAs(value, objectMapper, reference);
    }

    /**
     * Creates a converter that transforms input to the specified type reference using
     * the default JSON ObjectMapper. This is useful for converting to generic types.
     *
     * @param <T>       the source type
     * @param <R>       the target type
     * @param reference the type reference for the target type
     * @return a converter from T to R using JSON deserialization
     */
    public static <T, R> Converter<T, R> jsonReaderAs(TypeReference<R> reference) {
        return Converters.jsonReaderAs(Converters.DEFAULT_JSON_CONVERTER, reference);
    }

    /**
     * Creates a converter that transforms input to the specified type reference using
     * the provided ObjectMapper. This is useful for converting to generic types.
     *
     * @param <T>          the source type
     * @param <R>          the target type
     * @param objectMapper the ObjectMapper to use for deserialization
     * @param reference    the type reference for the target type
     * @return a converter from T to R using JSON deserialization
     */
    public static <T, R> Converter<T, R> jsonReaderAs(ObjectMapper objectMapper, TypeReference<R> reference) {
        return value -> Json.readAs(value, objectMapper, reference);
    }

    /**
     * Creates a converter that transforms byte arrays to objects of the specified class
     * using JSON deserialization with the default ObjectMapper.
     *
     * @param <T> the target type
     * @param cls the class reference for the target type
     * @return a converter from byte[] to T using JSON deserialization
     */
    public static <T> Converter<byte[], T> jsonReadFromBytes(Class<T> cls) {
        return Converters.jsonReaderAs(cls);
    }

    /**
     * Creates a converter that transforms byte arrays to objects of the specified type
     * reference using JSON deserialization with the default ObjectMapper.
     *
     * @param <T>       the target type
     * @param reference the type reference for the target type
     * @return a converter from byte[] to T using JSON deserialization
     */
    public static <T> Converter<byte[], T> jsonReadFromBytes(TypeReference<T> reference) {
        return Converters.jsonReaderAs(reference);
    }

    /**
     * Creates a converter that transforms objects to byte arrays using JSON serialization
     * with the default ObjectMapper.
     *
     * @param <T> the source type
     * @return a converter from T to byte[] using JSON serialization
     */
    public static <T> Converter<T, byte[]> jsonWriteAsBytes() {
        return Converters.jsonWriteAsBytes(Converters.DEFAULT_JSON_CONVERTER);
    }

    /**
     * Creates a converter that transforms objects to byte arrays using JSON serialization
     * with the provided ObjectMapper.
     *
     * @param <T>          the source type
     * @param objectMapper the ObjectMapper to use for serialization
     * @return a converter from T to byte[] using JSON serialization
     */
    public static <T> Converter<T, byte[]> jsonWriteAsBytes(ObjectMapper objectMapper) {
        return value -> Json.writeAs("bytes", value, objectMapper);
    }

    /**
     * Creates a converter that transforms objects to JSON strings using
     * the default ObjectMapper.
     *
     * @return a converter from Object to String using JSON serialization
     */
    public static Converter<String, Object> jsonWriteAsString() {
        return Converters.jsonWriteAsString(Converters.DEFAULT_JSON_CONVERTER);
    }

    /**
     * Creates a converter that transforms objects to JSON strings using
     * the provided ObjectMapper.
     *
     * @param objectMapper the ObjectMapper to use for serialization
     * @return a converter from Object to String using JSON serialization
     */
    public static Converter<String, Object> jsonWriteAsString(ObjectMapper objectMapper) {
        return value -> Json.writeAsString(value, objectMapper);
    }

    /**
     * Creates a converter that transforms objects to JsonNode tree using
     * the default ObjectMapper.
     *
     * @param <T> the source type
     * @return a converter from T to JsonNode using JSON serialization
     */
    public static <T> Converter<T, JsonNode> jsonWriteAsTree() {
        return Converters.jsonWriteAsTree(Converters.DEFAULT_JSON_CONVERTER);
    }

    /**
     * Creates a converter that transforms objects to JsonNode tree using
     * the provided ObjectMapper.
     *
     * @param <T>          the source type
     * @param objectMapper the ObjectMapper to use for serialization
     * @return a converter from T to JsonNode using JSON serialization
     */
    public static <T> Converter<T, JsonNode> jsonWriteAsTree(ObjectMapper objectMapper) {
        return value -> Json.writeAsTree(value, objectMapper);
    }

    /**
     * Creates a converter that transforms HTML strings to Jsoup Document objects.
     * This is useful for HTML parsing and manipulation.
     *
     * @return a converter from String to Document using Jsoup
     */
    public static Converter<String, Document> document() {
        return Jsoup::parse;
    }

    /**
     * Creates a converter that transforms a byte array representing Excel data into a list of objects
     * using the provided {@link XslxRowMapper}. The mapper is used to validate the headers and map
     * each row from the Excel sheet to the target object type.
     *
     * @param <R>       the type of objects contained in the resulting list
     * @param rowMapper the mapper responsible for validating the headers and mapping each row
     * @return a converter from byte[] to List(R) that processes Excel data
     */
    public static <R> Converter<byte[], List<R>> xslx(XslxRowMapper<R> rowMapper) {
        return excelData -> Xslx.convert(excelData, rowMapper);
    }

}
