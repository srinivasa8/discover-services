package com.example.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@Component
public class AsyncConfig {

    @Autowired
    private final Environment env;

    public AsyncConfig(Environment env) {
        this.env = env;
    }

    @Bean("taskExecutor")
    public Executor TaskExecutor(){
        ThreadPoolTaskExecutor executor  = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(env.getProperty("custom.thread.pool.core-size")));
        executor.setMaxPoolSize(Integer.parseInt(env.getProperty("custom.thread.pool.max-size")));
        executor.setQueueCapacity(Integer.parseInt(env.getProperty("custom.thread.pool.queue-capacity")));
        executor.setThreadNamePrefix(env.getProperty("custom.thread.pool.thread-name-prefix"));
        executor.initialize();
        return executor;
    }

}
