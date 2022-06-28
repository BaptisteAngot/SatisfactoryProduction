package com.satisfactory.production;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductionApplication{

    private static final Logger LOG = LoggerFactory.getLogger(ProductionApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(ProductionApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }
}
