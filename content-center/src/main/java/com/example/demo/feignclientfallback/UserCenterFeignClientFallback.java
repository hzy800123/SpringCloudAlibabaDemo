package com.example.demo.feignclientfallback;

import com.example.demo.domain.entity.user.Response;
import com.example.demo.domain.entity.user.User;
import com.example.demo.feignclient.UserCenterFeignClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {
    @Override
    public List<Response> findByIdListInBatch(List<Map<String, String>> requestAPIBodyList) {
        return new ArrayList<>();
    }

    @Override
    public List<User> findByIdList(List<Integer> idList) {
        return new ArrayList<>();
    }

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
