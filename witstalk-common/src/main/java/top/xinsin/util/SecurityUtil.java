package top.xinsin.util;

import com.alibaba.fastjson2.JSONObject;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.xinsin.constants.CacheConstants;
import top.xinsin.constants.TokenConstants;
import top.xinsin.entity.LoginUser;

import java.util.Enumeration;
import java.util.Objects;

public class SecurityUtil {

    public static LoginUser getLoginUser() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            throw new RuntimeException("无法获取请求对象");
        }
        
        // 获取token
        String token = null;
        Enumeration<String> headers = request.getHeaders(TokenConstants.AUTHENTICATION);
        if (headers != null && headers.hasMoreElements()) {
            String requestToken = headers.nextElement();
            // 检查是否包含前缀并正确移除
            if (requestToken != null && requestToken.startsWith(TokenConstants.PREFIX)) {
                token = requestToken.substring(TokenConstants.PREFIX.length()).trim();
            } else {
                token = requestToken;
            }
        }
        
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("未提供有效的认证令牌");
        }
        
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
                    .setUserId(jsonObject.getLong("id"));
        } catch (MalformedJwtException e) {
            // 处理JWT格式错误
            throw new RuntimeException("无效的JWT令牌格式", e);
        } catch (Exception e) {
            // 处理其他异常
            throw new RuntimeException("获取用户信息失败", e);
        }
    }

    public static HttpServletRequest getRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }
}
