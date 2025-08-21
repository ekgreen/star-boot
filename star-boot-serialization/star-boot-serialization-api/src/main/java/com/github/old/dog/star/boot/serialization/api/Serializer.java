package com.github.old.dog.star.boot.serialization.api;

import com.github.old.dog.star.boot.model.DataType;
import com.github.old.dog.star.boot.reflection.type.Ref;

/**
 * Universal serialization interface that provides methods for converting between
 * Java objects and their serialized representations in various formats.
 * <p>
 * This interface abstracts serialization operations without being tied to any
 * specific format (JSON, XML, etc.). It supports different input and output types
 * including String, byte array, and tree representations.
 * <p>
 * The interface implements a pattern that allows processing of serialized data
 * from different sources (String, byte array, InputStream) in a uniform way using
 * visitor pattern.
 */
public interface Serializer {

    /**
     * Serializes an object to a byte array containing the serialized data.
     *
     * @param value the object to be serialized
     * @return the serialized data as a byte array
     * @throws SerializationException if serialization fails
     */
    default byte[] writeAsBytes(Object value) {
        return this.writeAs(DataType.BYTE_ARRAY_TYPE, value);
    }

    /**
     * Serializes an object to a string containing the serialized data.
     *
     * @param value the object to be serialized
     * @return the serialized data as a String
     * @throws SerializationException if serialization fails
     */
    default String writeAsString(Object value) {
        return this.writeAs(DataType.STRING_TYPE, value);
    }

    /**
     * Core serialization method that handles different output types.
     * <p>
     * This method supports converting an object to different serialized representations:
     * <ul>
     *   <li>"bytes" - converts to a byte array</li>
     *   <li>"string" - converts to a String</li>
     *   <li>"tree" - converts to a tree node</li>
     * </ul>
     *
     * @param <R>   the return type, determined by the type parameter
     * @param type  the type of output ("bytes", "string", or "tree")
     * @param value the object to be serialized
     * @return the serialized data in the requested format
     * @throws SerializationException   if serialization fails
     * @throws IllegalArgumentException if an unknown type is specified
     */
    <R> R writeAs(String type, Object value);

    /**
     * Deserializes data to an object of the specified class.
     *
     * @param <T>   the input type containing serialized data (String, byte[], or InputStream)
     * @param <R>   the target type to deserialize to
     * @param value the serialized data to deserialize
     * @param clazz the Class object specifying the target type
     * @return the deserialized object
     * @throws SerializationException if deserialization fails
     */
    <T, R> R readAs(T value, Class<R> clazz);

    /**
     * Core deserialization method that processes serialized data using the visitor pattern.
     * <p>
     * This method handles the common logic for all deserialization operations, including
     * error handling and logging. It determines the appropriate visitor method to call
     * based on the type of the input value.
     *
     * @param <T>       the input type containing serialized data (String, byte[], or InputStream)
     * @param <R>       the target type to deserialize to
     * @param value     the serialized data to deserialize
     * @param reference a reference object providing type information for logging
     * @param visitor   the visitor that will process the serialized data
     * @return the deserialized object
     * @throws SerializationException if deserialization fails
     */
    <T, R> R readAs(T value, Ref reference, ReaderVisitor<R> visitor);
}
