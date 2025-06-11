package com.example.consulta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ConsultaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsultaApplication.class, args);
    }
}
