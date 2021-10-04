package ru.sberbank.reboot.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения
 */
@SpringBootApplication(scanBasePackages = "ru.sberbank.reboot")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


