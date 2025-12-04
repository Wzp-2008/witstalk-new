package top.xinsin.util;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public record Result<T>(Integer code, String msg, String error, T data, Long timestamp) {
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null, null, System.currentTimeMillis());
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", null, data, System.currentTimeMillis());
    }
    public static <T> Result<T> success(@NonNull String msg, T data) {
        return new Result<>(200, msg, null, data, System.currentTimeMillis());
    }
    public static <T> Result<T> fail(@NonNull String msg) {
        return new Result<>(500, msg, null, null, System.currentTimeMillis());
    }
    public static <T> Result<T> fail(@NonNull Integer code, @NonNull String msg) {
        return new Result<>(code, msg, null, null, System.currentTimeMillis());
    }
    public static <T> Result<T> fail(@NonNull Integer code, @NonNull String msg, @NonNull String error) {
        return new Result<>(code, msg, error, null, System.currentTimeMillis());
    }
    public static <T> Result<T> fail(@NonNull HttpStatus code, @NonNull String msg) {
        return fail(code.value(), msg);
    }

    public void writeToResponse(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json; charset=utf-8");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            writeToOutputStream(outputStream);
        } catch (IOException e) {
            log.trace("写出到流失败，", e);
        }
    }

    public void writeToOutputStream(OutputStream stream) throws IOException {
        stream.write(JSON.toJSONString(this).getBytes(StandardCharsets.UTF_8));
    }
}
