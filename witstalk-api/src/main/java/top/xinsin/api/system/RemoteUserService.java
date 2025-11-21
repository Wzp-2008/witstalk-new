package top.xinsin.api.system;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.xinsin.api.ServiceNameConstant;
import top.xinsin.api.system.domain.SysUser;
import top.xinsin.api.system.factory.RemoteUserFallbackFactory;
import top.xinsin.util.Result;

@FeignClient(contextId = "remoteUserService", value = ServiceNameConstant.WITSTALK_SYSTEM, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {
    @GetMapping("/getUserInfo")
    Result<SysUser> getUserInfo(@RequestParam("username") String username);

    @GetMapping("/register")
    Result<SysUser> register(@RequestParam("username") String username,@RequestParam("nickName") String nickName,@RequestParam("password") String password);
}
