package com.example.demo;

import com.example.demo.domain.entity.user.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/API")
@Slf4j
public class UserCenterAPIController {

    @GetMapping("/users/{id}")
    public User getUserInfo(@PathVariable Integer id) throws InterruptedException {
        log.info("API - Get User {}", id);
//        Thread.sleep(3000);

        return User.builder()
                    .id(id)
                    .userName("张三")
                    .role("Admin")
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
    }
}
