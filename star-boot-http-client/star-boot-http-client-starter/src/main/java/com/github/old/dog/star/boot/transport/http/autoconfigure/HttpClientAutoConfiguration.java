package com.github.old.dog.star.boot.transport.http.autoconfigure;

import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.old.dog.star.boot.transport.http.HttpClient;
import com.github.old.dog.star.boot.transport.http.ok.OkHttpClientDecorator;

@AutoConfiguration
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientAutoConfiguration {

    @ConditionalOnClass(
        value = {OkHttpClient.class}
    )
    @Configuration(proxyBeanMethods = false)
    public static class OkHttpConfiguration {

        @Bean
        @ConditionalOnMissingBean(HttpClient.class)
        public HttpClient httpClient(OkHttpClient okHttpClient) {
            return new OkHttpClientDecorator(okHttpClient);
        }

        @Bean
        @ConditionalOnMissingBean(OkHttpClient.class)
        public OkHttpClient okHttpClient(@NotNull HttpClientProperties properties) {
            return new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectionTimeout(3000), TimeUnit.MILLISECONDS)
                .readTimeout(properties.getReadTimeout(3000), TimeUnit.MILLISECONDS)
                .writeTimeout(properties.getWriteTimeout(3000), TimeUnit.MILLISECONDS)
                .protocols(List.of(
                    Protocol.HTTP_1_1,
                    Protocol.HTTP_2))
                .connectionSpecs(List.of(
                    ConnectionSpec.MODERN_TLS,
                    ConnectionSpec.COMPATIBLE_TLS))
                .build();
        }
    }

}
