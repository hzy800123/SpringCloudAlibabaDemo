package com.example.demo;

//import com.example.demo.rocketmq.MySource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.stream.messaging.Source;
//import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/stream")
public class StreamTest {

//    @Autowired
//    private Source source;

//    @Autowired
//    private MySource mySource;

    @PutMapping("/teststream")
    public String testStream() {
        log.info("test stream");
//        this.source.output()
//                 .send(
//                        MessageBuilder
//                            .withPayload("消息体")
//                            .build()
//                );

        return "Send stream successfully !";
    }


    @PutMapping("/testmysourcestream")
    public String testMySourceStream() {
        log.info("test my source stream");
//        this.mySource.output()
//                .send(
//                        MessageBuilder
//                            .withPayload("消息体 from my source")
//                            .build()
//                );
        return "Send stream from my source successfully !";
    }
}
