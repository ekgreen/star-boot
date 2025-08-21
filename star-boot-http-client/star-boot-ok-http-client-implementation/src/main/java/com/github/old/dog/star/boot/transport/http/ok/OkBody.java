package com.github.old.dog.star.boot.transport.http.ok;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.github.old.dog.star.boot.interfaces.Converter;
import com.github.old.dog.star.boot.transport.http.throwbles.BodyConversionException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import com.github.old.dog.star.boot.transport.http.call.Payload;

/**
 * Represents an HTTP response body with associated headers and cookies.
 * <p>
 * This class provides methods to extract information from an HTTP response, including
 * headers, cookies, response body metadata, and conversion utilities to transform
 * the raw response payload into a specific format using a converter.
 * <p>
 * The class is immutable and designed to encapsulate the HTTP response body along with
 * related components.
 */
@Slf4j
public class OkBody implements Payload {

    private final Map<String, Cookie> cookies;
    private final Headers headers;
    private final byte[] payload;

    public OkBody(List<Cookie> cookies, Headers headers, ResponseBody responseBody) {
        this.cookies = cookies
            .stream()
            .collect(Collectors.toMap(Cookie::name, Function.identity(), (_, ck2) -> ck2));
        this.headers = headers;
        this.payload = readBody(responseBody);
    }

    private byte[] readBody(ResponseBody responseBody) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Начинаем конвертацию тела ответа");
            }

            byte[] bytes = responseBody.bytes();

            if (log.isDebugEnabled()) {
                log.debug("Получены байты тела ответа, размер: {} байт", bytes.length);
            }

            return bytes;
        } catch (IOException e) {
            log.error("Ошибка при чтении тела ответа: {}", e.getMessage());
            throw new BodyConversionException("Не удалось прочитать тело ответа", e);
        }
    }

    @Override
    public long contentLength() {
        return payload != null ? payload.length : 0;
    }

    @Override
    public String cookies() {
        return cookies.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue().value())
            .collect(Collectors.joining("; "));
    }

    @Override
    public String header(String header) {
        return headers.get(header);
    }

    @Override
    public String cookie(String name) {
        return Optional.of(name)
            .map(cookies::get)
            .map(Cookie::value)
            .orElse(null);
    }

    @Override
    public List<String> headers(String header) {
        return headers.values(header);
    }

    @Override
    public <R> R convert(Converter<byte[], R> converter) {
        try {
            R result = converter.convert(payload);

            if (log.isDebugEnabled()) {
                log.debug("Конвертация успешно завершена");
            }

            return result;

        } catch (Exception e) {
            log.error("Ошибка при конвертации тела ответа: {}", e.getMessage());
            throw new BodyConversionException("Ошибка при конвертации тела ответа: " + e.getMessage(), e);
        }
    }

}
