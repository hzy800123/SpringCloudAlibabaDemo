package com.example.demo.sentinelblock;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

@Slf4j
public class TestControllerRestTemplateBlockHandler {
    /**
     * RestTemplate 整合 Sentinel，处理 限流 或者 降级
     * 这个 异常处理方法，参数和返回值 跟
     * org.springframework.http.client.ClientHttpRequestInterceptor#interceptor 方法一致，
     * 其中参数多出了一个 BlockException 参数用于获取 Sentinel 捕获的异常     *
     *
     * @param a
     * @param e
     * @return String
     */
    public static SentinelClientHttpResponse blockProcessing(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution,
            BlockException ex
    ) {
        System.err.println("Oops: " + ex.getClass().getCanonicalName());
        return new SentinelClientHttpResponse("custom blockProcessing");
    }
}
