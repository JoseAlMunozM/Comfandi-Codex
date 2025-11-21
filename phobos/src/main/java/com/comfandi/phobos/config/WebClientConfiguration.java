package com.comfandi.phobos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@Configuration
public class WebClientConfiguration {
    private static final int MAX_MEMORY_SIZE = 1024 * 1024;
    @Bean 
    public WebClient.Builder customWebClientBuilder() {
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE))
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies);
    }
}