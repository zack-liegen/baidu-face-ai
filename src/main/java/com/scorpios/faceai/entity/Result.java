package com.scorpios.faceai.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 *   接口返回数据格式
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success = true;

    /**
     * 返回处理消息
     */
    private String message = "";

    /**
     * 返回代码
     */
    private String code = "";

    /**
     * 返回数据对象 data
     */
    private T result;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    public Result() {
    }
    public Result(Integer code, String message) {
        this.code = code.toString();
        this.message = message;
    }

    public Result<T> success(String message) {
        this.message = message;
        this.code = "200";
        this.success = true;
        return this;
    }

    public static<T> Result<T> ok() {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode("200");
        return r;
    }

    public static<T> Result<T> ok(String msg) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode("200");
        r.setResult((T) msg);
        r.setMessage(msg);
        return r;
    }

    public static<T> Result<T> ok(T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode("200");
        r.setResult(data);
        return r;
    }
    public static<T> Result<T> error(String msg, T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(false);
        r.setCode("500");
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static<T> Result<T> error(String msg) {
        Result<T> r = new Result<T>();
        r.setSuccess(false);
        r.setCode("500");
        r.setMessage(msg);
        r.setResult(null);
        return r;
    }
}
