package top.xinsin.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.xinsin.api.system.domain.vo.SysUserAndAuthVO;
import top.xinsin.constants.CacheConstants;
import top.xinsin.domain.AuthUserRequest;
import top.xinsin.service.SysUserDetailsService;
import top.xinsin.util.JwtUtil;
import top.xinsin.util.Result;

import java.util.concurrent.TimeUnit;

@RestController
public class AuthController {

    private final SysUserDetailsService sysUserDetailsService;
    private final JwtUtil tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    public AuthController(JwtUtil tokenProvider, SysUserDetailsService sysUserDetailsService, RedisTemplate<String, String> redisTemplate) {
        this.tokenProvider = tokenProvider;
        this.sysUserDetailsService = sysUserDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/login")
    public Result<JSONObject> authenticateUser(@RequestBody AuthUserRequest loginRequest) {
        AuthUserRequest userDetails = sysUserDetailsService.login(loginRequest.getUsername(), loginRequest.getPassword());
        String jwt = tokenProvider.generateToken(userDetails.getUsername());
        redisTemplate.opsForValue().set(CacheConstants.LOGIN_TOKEN_KEY + userDetails.getUsername(), jwt, jwtExpirationInMs, TimeUnit.MILLISECONDS);
        userDetails.setPassword(null);
        redisTemplate.opsForValue().set(CacheConstants.LOGIN_USERINFO_KEY + userDetails.getUsername(), JSONObject.toJSONString(userDetails), jwtExpirationInMs, TimeUnit.MILLISECONDS);
        JSONObject jsonObject = new JSONObject();
        jsonObject.fluentPut("token", jwt);
        return Result.success(jsonObject);
    }

    @PostMapping("/register")
    public Result<JSONObject> registerUser(@RequestBody AuthUserRequest userDetails) {
        sysUserDetailsService.register(userDetails);
        return Result.success();
    }

    @PostMapping("/userInfo")
    public Result<SysUserAndAuthVO> userInfo() {
        SysUserAndAuthVO sysUser = sysUserDetailsService.userInfo();
        return Result.success(sysUser);
    }
}
