package com.github.old.dog.star.boot.serialization.json;


import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.old.dog.star.boot.reflection.type.Ref;
import com.github.old.dog.star.boot.serialization.api.ReaderVisitor;
import com.github.old.dog.star.boot.serialization.api.Serializer;

/**
 * A JSON-based implementation of the {@link Serializer} interface, providing
 * methods for serializing and deserializing objects to and from JSON format.
 * <p>
 * This class utilizes the Jackson library for handling JSON serialization
 * and deserialization. It provides convenient methods to convert Java objects
 * to JSON and vice versa, supporting various formats such as JSON strings
 * and tree representations.
 * <p>
 * The class is constructed with an instance of {@link ObjectMapper}, which
 * is used as the core component for processing JSON data. If no custom
 * ObjectMapper is provided, a default one from the {@link Json} utility class
 * is used.
 */
@RequiredArgsConstructor
public class JsonSerializer implements Serializer {

    private final ObjectMapper mapper;

    public JsonSerializer() {
        this.mapper = Json.getBasicJsonMapper();
    }

    public JsonNode writeAsTree(Object value) {
        return Json.writeAsTree(value, mapper);
    }

    @Override
    public <R> R writeAs(String type, Object value) {
        return Json.writeAs(type, value, mapper);
    }

    @Override
    public <T, R> R readAs(T value, Class<R> clazz) {
        return Json.readAs(value, mapper, clazz);
    }

    @Override
    public <T, R> R readAs(T value, Ref reference, ReaderVisitor<R> visitor) {
        return Json.readAs(value, reference, visitor);
    }

}
