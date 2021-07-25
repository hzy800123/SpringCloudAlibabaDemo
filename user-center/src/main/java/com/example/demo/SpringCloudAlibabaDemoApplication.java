package com.example.demo;

import com.example.demo.rocketmq.MySink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;

@SpringBootApplication
@EnableBinding({Sink.class, MySink.class})	// 添加注解，为了绑定 Stream RocketMQ
public class SpringCloudAlibabaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudAlibabaDemoApplication.class, args);
	}

}
