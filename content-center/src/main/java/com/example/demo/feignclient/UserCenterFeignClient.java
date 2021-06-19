package com.example.demo.feignclient;

import com.example.demo.configuration.UserCenterFeignConfiguration;
import com.example.demo.domain.entity.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "user-center")
// Feign 支持占位符，可以再配置文件里 定义属性值
@FeignClient(name = "${feign.name}")
//@FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class)
public interface UserCenterFeignClient {

    /**
     * 当调用 findById 方法时，会自动构造以下的URL，去调用 微服务提供者的API
     * 并把返回的结果转换为User对象
     * URL:
     * http://user-center/users/{id}
     * @param id
     * @param name
     * @return
     */
    @GetMapping("/API/users/{id}")
    User findById(@PathVariable Integer id);
//    @GetMapping("/API/users")
//    User findById(@RequestParam Integer id);

    // 带 多个参数 的API调用
    @GetMapping("/API/users")
    User findByIdAndName(@RequestParam Integer id, @RequestParam String name);

    // 带 多个参数 的API调用
//    @GetMapping("/API/query")
//    User findByIdAndName(@RequestParam Integer id, @RequestParam String name);
}
