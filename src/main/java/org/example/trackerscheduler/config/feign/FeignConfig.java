package org.example.trackerscheduler.config.feign;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.example.trackerscheduler.config.internal.InternalProperties;
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
