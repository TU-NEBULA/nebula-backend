package com.team_nebula.nebula.global.config;

import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public Bucket autocompleteBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(20)
                        .refillGreedy(20, Duration.ofMinutes(1))
                )
                .build();
    }

}

