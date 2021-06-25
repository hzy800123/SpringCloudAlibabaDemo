package com.example.demo.feignclientfallback;

import com.example.demo.domain.entity.user.User;
import com.example.demo.feignclient.UserCenterFeignClient;
import org.springframework.stereotype.Component;

@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {
    @Override
    public User findById(Integer id) {
        User user = new User();
        user.setId(0);
        user.setUserName("默认用户 User");
        return user;
    }

    @Override
    public User findByIdAndName(Integer id, String name) {
        User user = new User();
        user.setId(0);
        user.setUserName("默认用户 User");
        return user;
    }
}
