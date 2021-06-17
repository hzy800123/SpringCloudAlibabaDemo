package com.example.demo.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * Feign 的配置类
 * 这个类不能加上 @Configuration 注解
 * 否则，必须挪到 @ComponentScan 能扫描到的包以外
 */
public class UserCenterFeignConfiguration {
    @Bean
    public Logger.Level level() {
        // 让Feign打印所有的请求的细节
//        return Logger.Level.FULL;
        return Logger.Level.BASIC;
    }

}
