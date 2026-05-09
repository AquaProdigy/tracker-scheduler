package org.example.trackerscheduler.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.example.trackerscheduler.config.internal.InternalProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final InternalProperties internalProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate ->
                requestTemplate.header(internalProperties.getHeaderName(), internalProperties.getApiKey());
    }
}
