package org.example.trackerscheduler.client;

import org.example.trackerscheduler.config.feign.FeignConfig;
import org.example.trackerscheduler.dto.user.InternalUserEmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "tracker-auth", url = "${services-url.auth-url}",
        configuration = FeignConfig.class)
public interface UserEmailServiceClient {

    @PostMapping("/internal/users/email")
    List<InternalUserEmailDto> getEmailByIds(@RequestBody List<Long> ids);

}
