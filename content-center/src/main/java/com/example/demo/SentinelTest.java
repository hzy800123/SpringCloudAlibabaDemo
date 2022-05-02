package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class SentinelTest {
    /**
     * 测试 Sentinel 限流规则，流控模式：关联
     * 当 关联资源（/test/writeuserinfoAPI）的访问量达到阈值时，(写）
     * 指定的资源（/test/readuserinfoAPI）会被限流处理。（读）
     * Note：
     * 目的：保护 关联资源(写） ！
     * 方法：限制 指定资源（读）的访问量（限流处理）！
     */
    public static void main1(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        String targetURL = "http://localhost:8090/test/writeuserinfoAPI";

        for (int i=0; i<10000; i++) {
            String object = restTemplate.getForObject(targetURL, String.class);
            log.info("Sleep 500ms here...");
            Thread.sleep(500);
        }
    }

    /**
     * 测试 Sentinel 限流规则，限流效果：排队等待（匀速排队）
     * 当 指定资源（/test/test-a）的访问量达到阈值时，
     * 严格控制请求通过的间隔时间，也即是让请求以均匀的速度通过
     * 适用于 应对突发流量的场景，系统一会儿比较忙，一会儿比较闲，
     * 希望系统 在空闲的时候 处理之前的请求，而不是 直接拒绝请求。
     *
     * 设置参数：超时时间
     * 直到 如果请求等待 超过"超时时间"（e.g. 10000 ms/毫秒），则丢弃请求。
     */
    public static void main2(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String targetURL = "http://localhost:8090/test/test-a";

        for (int i=0; i<100; i++) {
            String object = restTemplate.getForObject(targetURL, String.class);
            log.info("---", object, "---");
        }
    }


    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        String targetURL = "http://localhost:8090/test/1";

        for (int i=0; i<10000; i++) {
            String object = restTemplate.getForObject(targetURL, String.class);
            log.info("---> {}", object);
//            log.info("Sleep 500ms here...");
//            Thread.sleep(500);
        }
    }
}
