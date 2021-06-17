package com.example.demo;

import com.example.demo.configuration.UserCenterFeignConfiguration;
import com.example.demo.configuration.UserCenterRibbonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//Feign全局配置（Java代码方式）
//@EnableFeignClients(defaultConfiguration = UserCenterFeignConfiguration.class)

@EnableFeignClients
public class SpringCloudAlibabaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudAlibabaDemoApplication.class, args);
	}

	// 在Spring容器中，创建一个对象，类型是 RestTemplate，名称/ID是 restTemplate
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
