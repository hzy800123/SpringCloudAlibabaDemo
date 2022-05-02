//package com.example.demo;
//
//import com.example.demo.contentcenter.dao.user.UserMapper;
//import com.example.demo.contentcenter.domain.entity.user.User;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.apache.rocketmq.spring.support.RocketMQHeaders;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Date;
//import java.util.UUID;
//
//@Slf4j
//@RestController
//@RequestMapping("/mq")
////@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class RocketMQTest {
//
//    @Autowired
//    private RocketMQTemplate rocketMQTemplate;
//
//    @Autowired
//    private UserMapper userMapper;
//
//
//    /**
//     * 测试 RocketMQ 的普通消息 传递
//     * rocketMQTemplate.convertAndSend()
//     *
//     * @return String
//     */
//    @PutMapping("/user/{id}")
//    public String testMQSendUser(@PathVariable Integer id) {
//        log.info("/mq/user/{}", id);
//
//        this.userMapper.insert(
//                User.builder()
////                .id(id)
//                .userName("John")
//                .role("admin")
//                .createTime(new Date())
//                .updateTime(new Date())
//                .build()
//        );
//
//        User user = this.userMapper.selectByPrimaryKey(id);
//        log.info("user: {}", user);
//
//        this.rocketMQTemplate.convertAndSend(
//                "test-topic-center",    // Topic
//                User.builder()
//                    .id(id)
//                    .userName("Jack")
//                    .role("admin")
//                    .createTime(new Date())
//                    .updateTime(new Date())
//                    .build());
//
//        return "/user/" + id;
//    }
//
//    /**
//     * 测试 RocketMQ 的事务消息，以及 RocketMQ Transaction Log表 的作用
//     * rocketMQTemplate.sendMessageInTransaction()
//     *
//     * @return String
//     */
//    @PutMapping("/addusertransactionmsg/{id}")
//    public String testMQAddUser(@PathVariable Integer id) {
//        log.info("/mq/addusertransactionmsg/{}", id);
//        String transactionId = UUID.randomUUID().toString();
//
//        this.rocketMQTemplate.sendMessageInTransaction(
//                "tx-add-user-group",    // Group
//                "test-topic-center",        // Topic
//                MessageBuilder
//                    .withPayload(
//                        User.builder()
//                        .id(id)
//                        .userName("John")
//                        .role("admin")
//                        .createTime(new Date())
//                        .updateTime(new Date())
//                        .build()
//                    )
//                    // 使用 Header 传参数
//                    .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
//                    .build(),
//                null
//        );
//
//        return "added user " + id + " and sent out one transaction message";
//    }
//}
