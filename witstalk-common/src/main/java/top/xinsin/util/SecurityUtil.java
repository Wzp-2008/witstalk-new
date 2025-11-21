package top.xinsin.util;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.xinsin.constants.CacheConstants;
import top.xinsin.constants.TokenConstants;
import top.xinsin.entity.LoginUser;

import java.util.Objects;

public class SecurityUtil {

    public static LoginUser getLoginUser() {
        String requestToken = Objects.requireNonNull(getRequest()).getHeaders(TokenConstants.AUTHENTICATION).nextElement();
        String token = requestToken.replaceAll(TokenConstants.PREFIX, "");
        StringRedisTemplate redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        JwtUtil jwtUtil = SpringContextHolder.getBean(JwtUtil.class);
        String username = jwtUtil.getFromJWT(token).getSubject();
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForValue().get(CacheConstants.LOGIN_USERINFO_KEY + username));
        assert jsonObject != null;
        return new LoginUser()
                .setUsername(jsonObject.getString("username"))
                .setNickName(jsonObject.getString("nickName"))
                .setUserId(jsonObject.getLong("id"));
    }

    public static HttpServletRequest getRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }
}
