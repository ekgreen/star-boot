package com.github.old.dog.star.boot.transport.http.ok;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.github.old.dog.star.boot.transport.http.body.ByteArray;
import com.github.old.dog.star.boot.transport.http.body.FormBody;
import lombok.RequiredArgsConstructor;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import com.github.old.dog.star.boot.transport.http.HttpClient;
import com.github.old.dog.star.boot.transport.http.body.Body;
import com.github.old.dog.star.boot.transport.http.call.Get;
import com.github.old.dog.star.boot.transport.http.call.Payload;
import com.github.old.dog.star.boot.transport.http.call.Post;

/**
 * A decorator for the OkHttpClient that implements the HttpClient interface.
 * <p>
 * This class provides an abstraction layer over the OkHttpClient, allowing it
 * to be used as an implementation of the HttpClient interface. It includes
 * support for executing HTTP GET and POST requests while supporting functionalities
 * like setting headers, query parameters, and request bodies.
 * <p>
 * Responsibilities:
 * - Handles the construction and execution of HTTP requests.
 * - Translates application-defined request and response objects into OkHttp-compliant formats.
 * - Provides error handling and exception wrapping for HTTP request execution.
 */
@RequiredArgsConstructor
public class OkHttpClientDecorator implements HttpClient {

    private static final byte[] EMPTY_BODY
        = new byte[0];

    private final OkHttpClient origin;

    @Override
    public Payload post(Post post) {
        Request request = post(post.getUrl(), post.getBody(), post.getHeaders());
        return executeSync(request);
    }

    private Request post(
        String url,
        Body body,
        Map<String, String> headers) {
        // noinspection DataFlowIssue
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        Request.Builder post = new Request.Builder()
            .url(urlBuilder.build().toString())
            .post(makeOkBody(body));

        addHeaders(post, headers);
        return post.build();
    }

    @Override
    public Payload get(Get get) {
        Request request = get(get.getUrl(), get.getQueryParams(), get.getHeaders());
        return executeSync(request);
    }

    private Request get(String url, Map<String, Object> queryParameters, Map<String, String> headers) {
        // noinspection DataFlowIssue
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        addQueryParameters(urlBuilder, queryParameters);

        Request.Builder get = new Request.Builder()
            .url(urlBuilder.build().toString())
            .get();

        addHeaders(get, headers);
        return get.build();
    }

    private Payload executeSync(Request request) {
        try (final Response response = origin.newCall(request)
            .execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                List<Cookie> cookies = Cookie.parseAll(request.url(), response.headers());
                return new OkBody(cookies, response.headers(), body);
            }

            throw new RuntimeException("не удалось обработать запрос: " + response);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private void addHeaders(Request.Builder builder, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
    }

    private void addQueryParameters(HttpUrl.Builder builder, Map<String, Object> queryParameters) {
        for (String key : queryParameters.keySet()) {
            builder.addQueryParameter(key, String.valueOf(queryParameters.get(key)));
        }
    }

    private RequestBody makeOkBody(Body body) {
        Function<Body, RequestBody> maker = switch (body) {
            case null -> this::makerFromNull;
            case ByteArray _ -> this::makerFromBuffer;
            case FormBody _ -> this::makerFromForm;
            default -> throw new IllegalArgumentException("unknow body type");
        };

        return maker.apply(body);
    }

    private RequestBody makerFromForm(Body body) {
        final okhttp3.FormBody.Builder builder
            = new okhttp3.FormBody.Builder();

        ((FormBody) body).getData()
            .forEach(data -> builder.add(data.getKey(), data.getValue()));

        return builder.build();
    }

    private RequestBody makerFromNull(Body body) {
        return RequestBody.create(OkHttpClientDecorator.EMPTY_BODY);
    }

    private RequestBody makerFromBuffer(Body body) {
        return RequestBody.create(((ByteArray) body).getArr());
    }
}
