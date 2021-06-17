package com.example.demo;

import com.example.demo.domain.entity.user.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/API")
@Slf4j
public class UserCenterAPIController {

//    @GetMapping("/users/{id}")
    @GetMapping("/users")
//    public User getUserInfo(@PathVariable Integer id) throws InterruptedException {
//    public User getUserInfo(@RequestParam Integer id) throws InterruptedException {
    public User getUserInfo(@RequestParam Integer id, @RequestParam String name) throws InterruptedException {
        log.info("API - Get User {}", id);
//        Thread.sleep(3000);

        return User.builder()
                    .id(id)
//                    .userName("张三")
                    .userName(name)
                    .role("Admin")
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
    }


    @GetMapping("/query")
    public User query(User user) {
        log.info("call /API/query - {}", user);
        return user;
    }


    @PostMapping("/post")
    public User post(@RequestBody User user) {
        log.info("call /API/post - {}", user);
        return user;
    }
}
