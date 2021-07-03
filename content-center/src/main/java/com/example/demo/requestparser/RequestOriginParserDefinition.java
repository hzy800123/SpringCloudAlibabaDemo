package com.example.demo.requestparser;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * RequestOriginParser 可以实现 区分来源，用于 Sentinel 的授权规则 的使用。
 *
 * Sentinel 授权规则：可以指定哪些请求可以访问（白名单），哪些不能访问（黑名单）。
 * 通过设置后，所有请求必须带上参数 origin=XXX，
 *
 * 以下面的配置为例，只有 origin=xxx 的请求才能通过访问（白名单），
 * 或者只有 origin=xxx 的请求不能通过访问（黑名单）。
 *
 * 如果不喜欢参数的方式，可以在代码中换成header传递，效果一样。
 *
 */
@Slf4j
//@Component  // 交由Spring管理，否则拿不到RequestOriginParser实现类，授权规则不生效。
public class RequestOriginParserDefinition implements RequestOriginParser {
    // 获取 调用方 标示信息，并返回。
    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 从请求参数中，获取名为 origin 的参数
        String serviceNameOrigin = request.getParameter("origin");
        StringBuffer url = request.getRequestURL();
        if (url.toString().endsWith("favicon.ico")) {
            return serviceNameOrigin;
        }

        // 如果获取不到 origin 参数，那么就抛异常
        if (StringUtils.isEmpty(serviceNameOrigin)) {
            throw new IllegalArgumentException("Origin Service Name must not be null");
        }

        // 使用 Request Header 去检查 浏览器的类型，如果不是使用 Chrome，那么就抛异常
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        log.info("User-Agent: {}", userAgent);

        if (userAgent.indexOf("chrome") > 0 ) {
            log.info("User-Agent: chrome");
        } else if (userAgent.indexOf("firefox") > 0) {
            log.info("User-Agent: firefox");
            throw new IllegalArgumentException("Please do not use Firefox browser to login. Thank you !");
        }

        log.info("serviceNameOrigin: {}", serviceNameOrigin);
        return serviceNameOrigin;
    }
}
