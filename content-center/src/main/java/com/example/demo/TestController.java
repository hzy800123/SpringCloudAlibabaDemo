package com.example.demo;

import com.example.demo.domain.entity.user.User;
import com.example.demo.feignclient.UserCenterFeignClient;
import com.example.demo.feignclient.UserCenterFeignClientMultiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    // Spring Cloud 的接口
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @Autowired
    private UserCenterFeignClientMultiParam userCenterFeignClientMultiParam;

    @GetMapping("/1")
    public String test() {
        return "test successfully !";
    }

    /**
     * 测试：服务发现，证明 内容中心 总能找到 用户中心
     * @return 用户中心 所有实例的地址信息
     */
    @GetMapping("/testnacos")
    public List<ServiceInstance> getInstance() {
        // 查询 指定服务的 所有实例的信息
        return this.discoveryClient.getInstances("user-center");
    }

    /**
     * 获取 当前服务发现组件里 组册的全部 微服务名称
     * @return 组册的全部 微服务名称
     */
    @GetMapping("/getservices")
    public List<String> getServices() {
        return this.discoveryClient.getServices();
    }

    /**
     * 获取 微服务（用户中心）的目标URL地址
     * @return
     */
//    @GetMapping("/getuserinfo/{id}")
    @GetMapping("/getuserinfo")
//    public User getUserCenterURL(@PathVariable Integer id) throws IllegalArgumentException {
    public User getUserCenterUL(@RequestParam Integer id) throws IllegalArgumentException{
        List<ServiceInstance> instances = this.discoveryClient.getInstances("user-center");

        // 获取 用户中心 的所有实例的信息 （例如：URL IP地址信息）
        List<String> targetURLfromNacos = instances.stream()
                .map(instance -> instance.getUri().toString() + "/API/users/{id}")
                .collect(Collectors.toList());

        int i = ThreadLocalRandom.current().nextInt(targetURLfromNacos.size());
        String targetURL = targetURLfromNacos.get(i);

        log.info("Nacos 目标 微服务 的地址URL: {}", targetURLfromNacos);
        log.info("随机 目标 微服务 的地址URL: {}", targetURL);

        // 调用 用户中心 的API，返回用户记录信息
//        User userInfo = restTemplate.getForObject(
        ResponseEntity<User> userInfo = this.restTemplate.getForEntity(
//                targetURLfromNacos,
                targetURL,
//                testURL,
                User.class,
                id
        );

        log.info("Status Code: {}", userInfo.getStatusCode());
        log.info(userInfo.getBody().toString());
//        log.info(userInfo.toString());

        return userInfo.getBody();
//        return userInfo;
    }

    /**
     * 通过 Ribbon，使用 负载均衡 LoadBalanced，获取 微服务（用户中心）的目标URL地址
     * @return
     */
    @GetMapping("/getuserinforibbon/{id}")
//    @GetMapping("/getuserinforibbon")
    public User getUserCenterByRibbon(@PathVariable Integer id) throws IllegalArgumentException {
//    public User getUserCenterByRibbon(@PathVariable Integer id) throws IllegalArgumentException {
        String targetURL = "http://user-center/API/users/{id}";

        // 调用 用户中心 的API，返回用户记录信息
//        User userInfo = this.restTemplate.getForObject(
        ResponseEntity<User> userInfo = this.restTemplate.getForEntity(
                targetURL,
                User.class,
                id
        );

        log.info("Status Code: {}", userInfo.getStatusCode());
        log.info(userInfo.getBody().toString());
//        log.info(userInfo.toString());

        return userInfo.getBody();
//        return userInfo;
    }

    /**
     * 通过 Feign，调用 微服务user-center（用户中心）
     * @return
     */
    @GetMapping("/getuserinfofeign/{id}")
//    @GetMapping("/getuserinfofeign")
    public User getUserCenterByFeign(@PathVariable Integer id) throws IllegalArgumentException {
//    public User getUserCenterByFeign(@RequestParam Integer id, @RequestParam String name) throws IllegalArgumentException {
//        String targetURL = "http://user-center/API/users/{id}";

        // 调用 用户中心 的API，返回用户记录信息
        log.info("通过Feign，调用 微服务user-center");
        User userInfo = this.userCenterFeignClient.findById(id);

        log.info(userInfo.toString());
        return userInfo;
    }


    /**
     * 通过 Feign，调用 微服务user-center（用户中心），并使用多个参数去调用 Get 方法
     * @return
     */
    @GetMapping("/getuserinfofeign")
    public User getUserCenterByFeign(User user) throws IllegalArgumentException {
//        String targetURL = "http://user-center/API/query";

        // 调用 用户中心 的API，返回用户记录信息
        log.info("通过Feign，调用 微服务user-center");
        User userInfo = this.userCenterFeignClientMultiParam.findByIdAndName(user);

        log.info(userInfo.toString());
        return userInfo;
    }


    /**
     * 通过 Feign，调用 微服务user-center（用户中心），并使用多个参数去调用 Post 方法
     * @return
     */
    @PostMapping("/postuserinfofeign")
    public User postUserCenterByFeign(@RequestBody User user) throws IllegalArgumentException {
//        String targetURL = "http://user-center/API/post";

        // 调用 用户中心 的API，返回用户记录信息
        log.info("通过Feign，调用 微服务user-center");
        User userInfo = this.userCenterFeignClientMultiParam.postByIdAndName(user);

        log.info(userInfo.toString());
        return userInfo;
    }
}
