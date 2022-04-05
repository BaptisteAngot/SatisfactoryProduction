package com.satisfactory.production;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.Scanner;

@SpringBootApplication
public class ProductionApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ProductionApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(ProductionApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner");
        do {
            Scanner scan= new Scanner(System.in);
            String text= scan.nextLine();
            System.out.println(text);
        }while (true);
    }
}
