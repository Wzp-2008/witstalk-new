package top.xinsin.util;

import com.alibaba.fastjson2.JSONObject;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.xinsin.constants.CacheConstants;
import top.xinsin.constants.TokenConstants;
import top.xinsin.entity.LoginUser;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;

public class SecurityUtil {


    private static String getTokenFromHeaders(@Nullable Enumeration<String> rawToken) {
        if (rawToken == null) {
            throw new RuntimeException("未提供有效的认证令牌");
        }
        return getTokenFromHeaders(rawToken.asIterator());
    }

    /**
     * 通过迭代器获取token
     * @param rawToken token迭代器
     * @return token值
     */
    private static String getTokenFromHeaders(@NonNull Iterator<String> rawToken) {
        if (rawToken.hasNext()) {
            String requestToken = rawToken.next();
            // 检查是否包含前缀并正确移除
            if (requestToken != null && requestToken.startsWith(TokenConstants.PREFIX)) {
                return requestToken.substring(TokenConstants.PREFIX.length()).trim();
            } else {
                return requestToken;
            }
        }
        throw new RuntimeException("未提供有效的认证令牌");
    }

    /**
     * 通过token获取用户
     * @param token token
     * @return token对应的用户
     */
    public static LoginUser getLoginUser(@NonNull String token) {
        try {
            StringRedisTemplate redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
            JwtUtil jwtUtil = SpringContextHolder.getBean(JwtUtil.class);

            // 验证token格式
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("无效的认证令牌");
            }

            String username = jwtUtil.getFromJWT(token).getSubject();
            String userInfoStr = redisTemplate.opsForValue().get(CacheConstants.LOGIN_USERINFO_KEY + username);

            if (userInfoStr == null) {
                throw new RuntimeException("用户信息不存在或已过期");
            }

            JSONObject jsonObject = JSONObject.parseObject(userInfoStr);
            if (jsonObject == null) {
                throw new RuntimeException("用户信息格式错误");
            }

            return new LoginUser()
                    .setUsername(jsonObject.getString("username"))
                    .setNickName(jsonObject.getString("nickName"))
                    .setRoles(jsonObject.getList("roles", String.class))
                    .setPermissions(jsonObject.getList("permissions", String.class))
                    .setUserId(jsonObject.getLong("id"));
        } catch (MalformedJwtException e) {
            // 处理JWT格式错误
            throw new RuntimeException("无效的JWT令牌格式", e);
        } catch (Exception e) {
            // 处理其他异常
            throw new RuntimeException("获取用户信息失败", e);
        }
    }
    public static LoginUser getLoginUser(@NonNull HttpServletRequest request) {
        // 获取token
        String token = getTokenFromHeaders(request.getHeaders(TokenConstants.AUTHENTICATION));
        return getLoginUser(token);
    }

    public static LoginUser getLoginUser(@NonNull NativeWebRequest request) {
        String[] headerValues = request.getHeaderValues(TokenConstants.AUTHENTICATION);
        if (headerValues == null) {
            throw new RuntimeException("未提供有效的认证令牌");
        }
        String token = getTokenFromHeaders(Arrays.stream(headerValues).iterator());
        return getLoginUser(token);
    }

    @Nullable
    public static LoginUser getLoginUser() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            throw new RuntimeException("无法获取请求对象");
        }
        try {
            return getLoginUser(request);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static HttpServletRequest getRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }
}
