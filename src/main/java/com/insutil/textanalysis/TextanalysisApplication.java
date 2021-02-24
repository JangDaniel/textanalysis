package com.insutil.textanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:analysis.properties")
@SpringBootApplication
public class TextanalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextanalysisApplication.class, args);
    }

}
