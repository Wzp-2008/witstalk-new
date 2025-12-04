package top.xinsin.util;

public record Result<T>(Integer code, String msg, String error, T data, Long timestamp) {
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null, null, System.currentTimeMillis());
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", null, data, System.currentTimeMillis());
    }
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, null, data, System.currentTimeMillis());
    }
    public static <T> Result<T> fail(String msg) {
        return new Result<>(500, msg, null, null, System.currentTimeMillis());
    }
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null, null, System.currentTimeMillis());
    }
    public static <T> Result<T> fail(Integer code, String msg, String error) {
        return new Result<>(code, msg, error, null, System.currentTimeMillis());
    }
}
