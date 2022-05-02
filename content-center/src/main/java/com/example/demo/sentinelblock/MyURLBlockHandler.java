package com.example.demo.sentinelblock;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UrlBlockHandler 可以提供 Sentinel 的各种 异常处理 的具体逻辑
 */
//@Component
public class MyURLBlockHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {

        ErrorMessage errorMsg = null;
        if (ex instanceof FlowException) {
            errorMsg = ErrorMessage.builder()
                            .status(101)
                            .message("限流了。。。")
                            .build();
        } else if (ex instanceof DegradeException) {
            errorMsg = ErrorMessage.builder()
                            .status(102)
                            .message("降级了。。。")
                            .build();
        } else if (ex instanceof ParamFlowException) {
            errorMsg = ErrorMessage.builder()
                            .status(103)
                            .message("热点参数限流了。。。")
                            .build();
        } else if (ex instanceof SystemBlockException) {
            errorMsg = ErrorMessage.builder()
                            .status(104)
                            .message("系统规则（负载/。。。不满足要求）。。。")
                            .build();
        } else if (ex instanceof AuthorityException) {
            errorMsg = ErrorMessage.builder()
                            .status(105)
                            .message("授权规则不通过。。。")
                            .build();
        }
        // http状态码
        response.setStatus(500);
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.setContentType("application/json;charset=utf-8");

        // Spring MVC 自带的jason操作工具，叫 jackson
        new ObjectMapper()
                .writeValue(
                        response.getWriter(),
                        errorMsg
                );
    }
}


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ErrorMessage {
    private Integer status;
    private String message;
}