package com.tdep.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tdep")
public class TdepNotifyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TdepNotifyServiceApplication.class, args);
    }
}
