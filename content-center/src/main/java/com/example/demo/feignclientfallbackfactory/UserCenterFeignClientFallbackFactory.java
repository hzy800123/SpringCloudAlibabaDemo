package com.example.demo.feignclientfallbackfactory;

import com.example.demo.domain.entity.user.Response;
import com.example.demo.domain.entity.user.User;
import com.example.demo.feignclient.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserCenterFeignClientFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable cause) {
        // 使用 匿名内部类 返回
        return new UserCenterFeignClient() {

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
                log.warn("远程调用 User-Center，被限流/降级了 ！", cause);
                User user = new User();
                user.setId(0);
                user.setUserName("默认用户 User");
                return user;
            }

            @Override
            public User findByIdAndName(Integer id, String name) {
                log.warn("远程调用 User-Center，被限流/降级了 ！", cause);
                User user = new User();
                user.setId(0);
                user.setUserName("默认用户 User");
                return user;
            }
        };
    }
}
