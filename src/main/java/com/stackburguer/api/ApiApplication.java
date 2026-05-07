package com.stackburguer.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.stripe.Stripe;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.stackburguer.api.repositories")  //onde está o repo do Postgres

@EntityScan(basePackages = "com.stackburguer.api.models")
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
