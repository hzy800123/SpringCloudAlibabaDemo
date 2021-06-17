package com.example.demo.feignclient;

import com.example.demo.domain.entity.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-center")
public interface UserCenterFeignClientMultiParam {
    @GetMapping("/API/query")
    User findByIdAndName(@SpringQueryMap User user);

    @PostMapping("/API/post")
//    @RequestMapping(value="/API/post", method= RequestMethod.POST)
    User postByIdAndName(@RequestBody User user);
}
