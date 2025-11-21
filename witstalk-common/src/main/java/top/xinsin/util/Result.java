package top.xinsin.util;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code = 200;
    private String msg = "success";
    private String error;
    private T data;
    private Long timestamp = System.currentTimeMillis();

    private Result() {}

    public static  <T> Result<T> success() {
        return new Result<T>();
    }
    public static  <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }
    public static  <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    public static <T> Result<T> fail(Integer code, String msg, String error) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setError(error);
        return result;
    }
}
