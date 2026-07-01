package com.event.event_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig
{

    @Bean
    RestClient bookingServiceRestClient(
            RestClient.Builder builder,
            @Value("${booking-service.base-url}") String bookingServiceBaseUrl
    )
    {
        return builder
                .baseUrl(bookingServiceBaseUrl)
                .build();
    }
}
