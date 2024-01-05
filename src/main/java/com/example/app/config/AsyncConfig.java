package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@Component
public class AsyncConfig {

    @Bean("taskExecutor")
    public Executor TaskExecutor(){
        ThreadPoolTaskExecutor executor  = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ds-thread-");
        executor.initialize();
        return executor;
    }
}
