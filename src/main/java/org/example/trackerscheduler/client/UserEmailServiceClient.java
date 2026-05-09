package org.example.trackerscheduler.client;

import org.example.trackerscheduler.config.FeignConfig;
import org.example.trackerscheduler.model.InternalUserEmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "tracker-auth", url = "${services.auth-url}",
        configuration = FeignConfig.class)
public interface UserEmailServiceClient {

    @PostMapping("${services-url.auth-endpoint-email}")
    List<InternalUserEmailDto> getEmailByIds(@RequestBody List<Long> ids);

}
