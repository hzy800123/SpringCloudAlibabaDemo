package com.example.demo.requestinterceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Slf4j
//@Component
public class ValidateInterceptorForID implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        log.info("Start to call ValidateInterceptorForID here ! ! !");

        // 模拟 黑名单 - Black List for 'id'
        String[] blackListNames = new String[]{"1", "3", "5"};
        List<String> blackListNamesList = Arrays.asList(blackListNames);

        //获取到进入时的request对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            String requestURI = attributes.getRequest().getRequestURI();
            String[] splitString = requestURI.split("/");
            String id = splitString[splitString.length - 1];

            log.info("request URI: {}", requestURI);

            if(blackListNamesList.contains(id)) {
                throw new IllegalArgumentException("Rejected - ID '" + id + "' exists in Black List ID");
            }
        }

    }
}
