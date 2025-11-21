package top.xinsin.config;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.xinsin.util.Result;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        Result<?> fail = Result.fail(500, "内部服务器错误",ex.getMessage());
        Map<String, String> map = JSONObject.parseObject(JSONObject.toJSONString(fail), Map.class);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
    }
}
