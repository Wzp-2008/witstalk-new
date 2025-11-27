package top.xinsin.config;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import top.xinsin.util.Result;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 通用的400错误处理（兜底）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        Result<Object> response = Result.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Object>> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        Result<Object> fail = Result.fail(500, "内部服务器错误",ex.getMessage());
        return new ResponseEntity<>(fail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
