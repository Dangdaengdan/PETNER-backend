package com.example.petner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PetnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetnerApplication.class, args);
    }

}
