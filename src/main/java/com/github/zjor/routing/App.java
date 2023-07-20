package com.github.zjor.routing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    @Bean
    public RouteFinderService routeFinderService() {
        return new RouteFinderService();
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
