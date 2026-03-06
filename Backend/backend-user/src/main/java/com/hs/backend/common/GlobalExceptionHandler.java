package com.hs.backend.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.error("系统错误：" + e.getMessage());
    }
}
