package com.example.demo.sentinelblock;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestControllerBlockHandler {
    /**
     * 处理 限流 或者 降级
     * 这个 异常处理方法，必须和上面的 受保护的testSentinelResourceAnnotation方法，
     * 具有相同的 输入参数 和 返回值
     *
     * @param a
     * @param e
     * @return String
     */
    public static String blockProcessing(String a, BlockException e) {
        log.warn("被限流，或被降级了 Block !", e);
        return "被限流，或被降级了 Block !";
    }
}
