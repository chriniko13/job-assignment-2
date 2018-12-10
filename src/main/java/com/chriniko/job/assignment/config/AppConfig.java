package com.chriniko.job.assignment.config;

import lombok.extern.log4j.Log4j2;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;

@Profile("development")

@Configuration

@ComponentScan("com.chriniko.job.assignment")

public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.setMappingFiles(Collections.singletonList("dozer_mapping.xml"));
        return dozerBeanMapper;
    }

    @Qualifier("io")
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor ioWorkers() {

        int processors = Runtime.getRuntime().availableProcessors();

        ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        return new ThreadPoolExecutor(
                processors,
                4 * processors,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(300),
                r -> {
                    Thread thread = defaultFactory.newThread(r);
                    thread.setUncaughtExceptionHandler(new DefaultExceptionHandler());
                    return thread;
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Qualifier("computation")
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor computationWorkers() {

        ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        return new ThreadPoolExecutor(
                25,
                100,
                2,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(500),
                r -> {
                    Thread thread = defaultFactory.newThread(r);
                    thread.setUncaughtExceptionHandler(new DefaultExceptionHandler());
                    return thread;
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Log4j2
    static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable t) {
            log.error("[threadName: "
                    + thread.getName()
                    + "] message: "
                    + t.getMessage()
                    + ", stackTrace: "
                    + Arrays.toString(t.getStackTrace()), t);
        }
    }
}
