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
public class UserCenterAPIController {

    @GetMapping("/users/{id}")
    public User getUserInfo(@PathVariable Integer id) throws InterruptedException {
//        return User.builder()
//                .id(id)
//                .userName("张三")
//                .role("Admin")
//                .createTime(new Date())
//                .updateTime(new Date())
//                .build();

        // log.info("Get Users {}", id);
        System.out.println("API - Get User " + id);

        User user = User.builder()
                        .id(id)
                        .userName("张三")
                        .role("Admin")
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();

//        Thread.sleep(3000);
        return user;
    }
}
