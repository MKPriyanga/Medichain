package com.cts.departmentrequest_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DepartmentrequestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DepartmentrequestServiceApplication.class, args);
		
	}

}
