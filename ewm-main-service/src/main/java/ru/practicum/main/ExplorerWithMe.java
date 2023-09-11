package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum"})
public class ExplorerWithMe {
    public static void main(String[] args) {
        SpringApplication.run(ExplorerWithMe.class, args);
    }
}
