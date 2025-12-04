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

@Data
@Slf4j
public class Result<T> {
    private Integer code = 200;
    private String msg = "success";
    private String error;
    private T data;
    private Long timestamp = System.currentTimeMillis();

    private Result() {}

    public static  <T> Result<T> success() {
        return new Result<>();
    }
    public static  <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }
    public static  <T> Result<T> success(@NonNull String msg, T data) {
        Result<T> result = new Result<>();
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> fail(@NonNull String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
    public static <T> Result<T> fail(@NonNull Integer code, @NonNull String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    public static <T> Result<T> fail(@NonNull Integer code, @NonNull String msg, @NonNull String error) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setError(error);
        return result;
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
