package com.cts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient // Allows Eureka to see this service
@EnableFeignClients    // Prepared for inter-service calls
public class ProductSupplierServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductSupplierServiceApplication.class, args);
	}

}
