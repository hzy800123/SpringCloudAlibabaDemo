package com.example.demo.sentinelurlcleaner;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * UrlCleaner 可以重新定义 资源名称
 * 例如：
 * 让 /test/1 与 /test/2 的返回值（资源名称）相同
 * 均返回资源名称为 /test/{number}
 *
 */
@Component
@Slf4j
public class MyURLCleaner implements UrlCleaner {
    @Override
    public String clean(String originUrl) {
        log.info("originUrl = {}", originUrl);

        // 让 /test/1 与 /test/2 的返回值相同
        // 均返回 /test/{number}
        String[] splitStr = originUrl.split("/");
        // 此时，splitStr = { "", "test", "1" }

        String newStr = Arrays.stream(splitStr)
                            .map(str -> {
                                if (NumberUtils.isNumber(str)) {
                                    return "{number}";
                                }
                                return str;
                            })
                            .reduce((a, b) -> (a + "/" + b))
                            .orElse("");

        log.info("newStr = {}", newStr);
        return newStr;
    }
}
