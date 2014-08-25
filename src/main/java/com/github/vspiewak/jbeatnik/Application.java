package com.github.vspiewak.jbeatnik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;

@EnableAutoConfiguration
@ComponentScan
public class Application {

    private final Logger log = LoggerFactory.getLogger(Application.class);

    @Inject
    private Environment env;

    @PostConstruct
    public void initApplication() throws IOException {

        log.info("Spring default profile(s) {}", Arrays.toString(env.getDefaultProfiles()));

        if (env.getActiveProfiles().length == 0) {
            log.warn("No Spring profile configured, running with default configuration");
        } else {
            log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}