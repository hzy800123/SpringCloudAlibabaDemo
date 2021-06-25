package com.example.demo.sentinelblock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestControllerFallbackHandler {
    /**
     * - Sentinel 1.5 可以处理 降级
     * - Sentinel 1.6 以上，可以处理 Throwable，就是不管任何异常发生，都可以自动进入本方法处理。
     *
     * 这个 回滚处理方法，必须和上面的 受保护的testSentinelResourceAnnotation方法，
     * 具有相同的 输入参数 和 返回值
     *
     * @param a
     * @param e
     * @return String
     */
    public static String fallbackProcessing(String a) {
        log.warn("被回滚了 Fallback !");
        return "被回滚了 Fallback !";
    }
}
