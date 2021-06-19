package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Feign 脱离 Nacos 和 Ribbon 也可以使用，需要指定 'url'
 */
@FeignClient(name = "baidu", url = "www.baidu.com")
public interface FeignClientWithoutNacosAndRibbon {
    @GetMapping
    public String getBaiduIndex();
}
