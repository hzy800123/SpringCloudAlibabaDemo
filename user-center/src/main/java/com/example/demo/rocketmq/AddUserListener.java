package com.example.demo.rocketmq;

import com.example.demo.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
//@RocketMQMessageListener(consumerGroup = "test-group2", topic = "test-topic-center")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddUserListener implements RocketMQListener<User> {
    @Override
    public void onMessage(User message) {
        // 当收到消息的时候，执行的业务
        log.info("AddUserListener - onMessage");
        User user = User.builder()
                        .id(message.getId())
                        .userName(message.getUserName())
                        .role(message.getRole())
                        .createTime(message.getCreateTime())
                        .updateTime(message.getUpdateTime())
                        .build();

        Date date = new Date();
        log.info("{} - Receive User - {}", date, user);
    }
}
