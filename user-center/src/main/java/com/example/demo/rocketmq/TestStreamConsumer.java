package com.example.demo.rocketmq;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.stream.annotation.StreamListener;
//import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
public class TestStreamConsumer {

//    @StreamListener(Sink.INPUT)
    public void receive(String messageBody) {
        log.info("通过 stream 收到消息： messageBody = {}", messageBody);
    }
}
