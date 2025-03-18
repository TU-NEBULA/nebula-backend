package com.team_nebula.nebula.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // 동시에 실행할 수 있는 최소 스레드 개수
        executor.setMaxPoolSize(10);  // 최대 스레드 개수
        executor.setQueueCapacity(100); // 큐 크기
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
