package edu.sdccd.cisc191.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "edu.sdccd.cisc191.server.repositories")
public class Server {


    public static void main(String[] args) {

        SpringApplication.run(Server.class, args);
    }


}
