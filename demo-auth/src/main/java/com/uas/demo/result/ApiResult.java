package com.uas.demo.result;

/**
 * 统一 API 响应格式（与 uas-common 的 ApiResult 保持一致）。
 * Demo 独立实现，不依赖 uas-common。
 */
public class ApiResult<T> {

    private int code;
    private String msg;
    private T data;

    public ApiResult() {}

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ---- 工厂方法 ----

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(200, "success", data);
    }

    public static <T> ApiResult<T> ok(String msg, T data) {
        return new ApiResult<>(200, msg, data);
    }

    public static <T> ApiResult<T> fail(int code, String msg) {
        return new ApiResult<>(code, msg, null);
    }

    // ---- getters / setters ----

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
