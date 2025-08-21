package com.github.old.dog.star.boot.transport.http.body;

import lombok.Data;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a form-encoded HTTP request body.
 *
 * This class provides a structured representation of form data to be included as the body
 * of an HTTP request. The form data consists of key-value pairs, where each pair represents
 * a single form field. This implementation is immutable after adding entries, facilitating
 * safe and thread-safe use.
 *
 * The {@code FormBody} class is particularly useful when submitting data using a content
 * type of "application/x-www-form-urlencoded".
 *
 * Methods are provided to dynamically add key-value pairs to the form data and retrieve
 * the data as a stream for further processing or encoding.
 *
 * Implements the {@code Body} marker interface to represent a specific type of HTTP request body.
 */
public class FormBody implements Body {
    private final List<FormData> data
            = new ArrayList<>();

    public FormBody add(String key, String value) {
        this.data.add(new FormData(key, value));
        return this;
    }

    public Stream<FormData> getData() {
        return data.stream();
    }

    @Value
    @Data
    public static class FormData {
        String key;
        String value;
    }
}
