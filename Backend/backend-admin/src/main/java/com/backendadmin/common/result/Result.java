package com.admin.backendadmin.common.result;

import com.admin.backendadmin.common.constant.ResultCodeConstant;
import lombok.Data;

/**
 * 统一返回结果集
 * 兼容多fail方法，同时提供通用方法，兼顾现有代码和扩展性
 */
@Data
public  class Result<T> {
    //状态码
    private Integer code;
    //提示信息
    private String msg;
    //响应数据
    private T data;


    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功-带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCodeConstant.SUCCESS, "操作成功", data);
    }

    /**
     * 成功-无数据
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败-系统异常
     */
    public static <T> Result<T> fail01(Integer code, String msg) {
        return new Result<>(ResultCodeConstant.ERROR, "系统异常", null);
    }

    /**
     * 资源不存在
     */
    public static <T> Result<T> fail02(Integer code, String msg) {
        return new Result<>(ResultCodeConstant.NOT_FOUND, "页面不存在", null);
    }

    /*
    * 业务异常
    * */
    public static <T> Result<T> fail03(Integer code, String msg) {
        return new Result<>(ResultCodeConstant.PARAM_ERROR, "参数错误", null);
    }

    public static <T> Result<T> fail04(Integer code, String msg) {
        return new Result<>(ResultCodeConstant.UNAUTHORIZED, "未登录", null);
    }

    public static <T> Result<T> fail05(Integer code, String msg) {
        return new Result<>(ResultCodeConstant.FORBIDDEN, "无权限访问", null);
    }


    /**
     * 通用失败-所有场景均可调用
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        // 可根据code自动匹配对应fail方法（核心优化点）
        if (code == ResultCodeConstant.PARAM_ERROR) {
            return Result.fail03(code, msg);
        } else if (code == ResultCodeConstant.ERROR) {
            return Result.fail04(code, msg);
        } else {
            return Result.fail05(code, msg); // 业务异常默认走fail04
        }
    }


}