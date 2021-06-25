package com.example.demo;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServiceTest {
    @SentinelResource("commonAPI")
    public String commonAPI() {
      log.info("Calling common API...");
      return "Calling common API...";
    }
}
