package com.hs.backend.exception;

import com.hs.backend.common.Result;
import com.hs.backend.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidException(Exception e) {
        log.error("参数校验异常：{}", e.getMessage());
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException ex) {
            if (!ex.getBindingResult().getAllErrors().isEmpty()) {
                message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            }
        } else if (e instanceof BindException ex) {
            if (!ex.getBindingResult().getAllErrors().isEmpty()) {
                message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            }
        }
        return Result.error(400, message);
    }

    /**
     * 权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限不足：{}", e.getMessage());
        return Result.error(403, "权限不足");
    }

    /**
     * 请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持：{}", e.getMessage());
        return Result.error(405, "请求方法不支持");
    }

    /**
     * 其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleOtherException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.error("系统繁忙，请稍后再试");
    }

    /**
     * 资源未找到异常（NoResourceFoundException）
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("资源未找到：{}", e.getMessage());
        return Result.error(404, "接口不存在：" + e.getMessage());
    }
}
