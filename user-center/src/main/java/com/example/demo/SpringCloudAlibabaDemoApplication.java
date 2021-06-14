package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
//@EnableDiscoveryClient
public class SpringCloudAlibabaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudAlibabaDemoApplication.class, args);
	}

	@PostConstruct
	public void init(){
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}
}
