package org.informatics.winterolympics;

import org.informatics.winterolympics.config.KeycloakProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(KeycloakProperties.class)
@SpringBootApplication
public class WinterOlympicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WinterOlympicsApplication.class, args);
    }

}
