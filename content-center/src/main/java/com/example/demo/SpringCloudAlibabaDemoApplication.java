package com.example.demo;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.example.demo.configuration.UserCenterFeignConfiguration;
import com.example.demo.configuration.UserCenterRibbonConfiguration;
import com.example.demo.rocketmq.MySource;
import com.example.demo.sentinelblock.TestControllerBlockHandler;
import com.example.demo.sentinelblock.TestControllerRestTemplateBlockHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

// 扫描mybatis哪些包里面的接口
@MapperScan("com.example.demo.contentcenter.dao")
@SpringBootApplication
//Feign全局配置（Java代码方式）
//@EnableFeignClients(defaultConfiguration = UserCenterFeignConfiguration.class)

@EnableFeignClients
@EnableBinding({Source.class, MySource.class})	// 添加注解，为了绑定 Stream RocketMQ
public class SpringCloudAlibabaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudAlibabaDemoApplication.class, args);
	}

	// 在Spring容器中，创建一个对象，类型是 RestTemplate，名称/ID是 restTemplate
	@Bean
	@LoadBalanced
	// 加上注解，就可以为 RestTemplate 整合 Sentinel
	@SentinelRestTemplate(
			// 存放blockHandler的类。对应的处理函数必须 static修饰，否则无法解析
			blockHandlerClass = TestControllerRestTemplateBlockHandler.class,
			blockHandler = "blockProcessing"
	)
//	@SentinelRestTemplate
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
