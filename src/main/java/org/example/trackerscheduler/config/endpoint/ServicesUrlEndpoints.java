package org.example.trackerscheduler.config.endpoint;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "services-url")
@Configuration
@Getter
@Setter
@Validated
public class ServicesUrlEndpoints {
    @NotBlank
    private String authUrl;
    @NotBlank
    private String taskUrl;
}
