package com.admin.backendadmin.common.exception;

import com.admin.backendadmin.common.constant.ResultCodeConstant;
import com.admin.backendadmin.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一捕获Controller层的所有异常，返回标准化的Result格式
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.admin.backendadmin.server.controller")
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常（最常用）
     * 自动根据异常code匹配对应Result.fail方法
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理所有未捕获的系统异常（兜底）→ 固定调用fail01
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleSystemException(Exception e) {
        log.error("系统异常：", e); // 打印完整堆栈，便于排查问题
        // 直接调用fail01（系统异常专属）
        return Result.fail01(ResultCodeConstant.ERROR, "服务器内部错误，请联系管理员");
    }
}