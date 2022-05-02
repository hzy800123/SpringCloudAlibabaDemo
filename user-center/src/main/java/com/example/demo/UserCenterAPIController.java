package com.example.demo;

import com.example.demo.domain.entity.user.User;

import com.example.demo.domain.entity.user.UserDAO;
import com.example.demo.domain.entity.user.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/API")
@Slf4j
public class UserCenterAPIController {

    @Autowired
    UserService userService;

    @Data
    class Response {
        String serialNo = "";
        User user;
    }

    @PostMapping("/users/id-list-in-batch")
    public List<Response> getUserInfoByIdListInBatch(
            @RequestBody List<Map<String, String>> requestAPIBodyList
    )
            throws InterruptedException {
        long startTime = System.nanoTime();
        log.info("In Batch - requestAPIBodyList: {}", requestAPIBodyList);
        List<Response> userListResultInBatch = new ArrayList<>();
        requestAPIBodyList.forEach(
                requestAPIBodyItem -> {
                    String serialNo = requestAPIBodyItem.get("serialNo");
                    String userId = requestAPIBodyItem.get("userId");
//                    log.info("API - Get User with " +
//                            "serialNo = {}, Id = {}",
//                            serialNo, userId);

                    User user = userService.getUserRecord(Integer.parseInt(userId));
                    if(user == null) {
                        user = new User();
                    }
                    Response response = new Response();
                    response.setSerialNo(serialNo);
                    response.setUser(user);
                    userListResultInBatch.add(response);
                }
        );
        log.info("Processed to get Users in Batch, total count = {}", userListResultInBatch.size());
        long endTime = System.nanoTime();
        calculateAndDisplayCostTime(startTime, endTime, "Get User Records in Batch from MySQL");

        return userListResultInBatch;
    }


    private void calculateAndDisplayCostTime(long startTime, long endTime, String description) {
        long usedTime = endTime - startTime;
        long millisecondTime = TimeUnit.MILLISECONDS.convert(usedTime, TimeUnit.NANOSECONDS);
        log.warn(description + " - {} milliseconds", millisecondTime);
    }


    @PostMapping("/users/id-list")
    public List<User> getUserInfoByIdList(
            @RequestBody List<Integer> idList
    )
            throws InterruptedException {
        log.info("idList: {}", idList);
        List<User> userListResult = new ArrayList<>();
        idList.forEach(
                id -> {
                    log.info("API - Get User with Id = {}", id);
                    User user = userService.getUserRecord(id);
                    if(user == null) {
                        user = new User();
                    }
                    userListResult.add(user);
                }
        );
        log.info("Processed to get Users, total count = {}", userListResult.size());
        return userListResult;
    }


    @GetMapping("/users/{id}")
    public User getUserInfo(@PathVariable Integer id) throws InterruptedException {
//    @GetMapping("/users")
//    public User getUserInfo(@RequestParam Integer id) throws InterruptedException {
//    public User getUserInfo(@RequestParam Integer id, @RequestParam String name) throws InterruptedException {
        log.info("API - Get User with Id = {}", id);
//        Thread.sleep(3000);

        User user = userService.getUserRecord(id);
        if(user == null)
            user = new User();
        log.info("Get User Record: {}", user.toString());

        return user;
//        return User.builder()
//                    .id(id)
//                    .userName("张三")
////                    .userName(name)
//                    .role("Admin")
//                    .createTime(new Date())
//                    .updateTime(new Date())
//                    .build();
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
