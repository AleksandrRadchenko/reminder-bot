package me.alsturm.timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class TimerMain {
    public static void main(String[] args) {
        SpringApplication.run(TimerMain.class, args);
    }
}
