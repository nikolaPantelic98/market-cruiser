package com.marketcruiser.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.marketcruiser.common.entity", "com.marketcruiser.admin.user"})
public class MarketCruiserBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketCruiserBackEndApplication.class, args);
    }

}
