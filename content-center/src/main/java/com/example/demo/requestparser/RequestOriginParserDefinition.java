package com.example.demo.requestparser;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Sentinel 授权规则：可以指定哪些请求可以访问（白名单），哪些不能访问（黑名单）。
 * 通过设置后，所有请求必须带上参数 origin=XXX，
 *
 * 以下面的配置为例，只有 origin=xxx 的请求才能通过访问（白名单），或者只有 origin=xxx 的请求不能通过访问（黑名单）。
 * 如果不喜欢参数的方式，可以在代码中换成header传递，效果一样。
 *
 */
// @Component  // 交由Spring管理，否则拿不到RequestOriginParser实现类，授权规则不生效。
public class RequestOriginParserDefinition implements RequestOriginParser {
    // 获取 调用方 标示信息， 并返回。
    @Override
    public String parseOrigin(HttpServletRequest request) {
        String serviceNameOrigin = request.getParameter("origin");
        StringBuffer url = request.getRequestURL();
        if (url.toString().endsWith("favicon.ico")) {
            return serviceNameOrigin;
        }

        if (StringUtils.isEmpty(serviceNameOrigin)) {
            throw new IllegalArgumentException("Origin Service Name must not be null");
        }

        return serviceNameOrigin;
    }
}
