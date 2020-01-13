package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@SpringBootApplication
@EnableFeignClients
public class TransactionServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }


}
