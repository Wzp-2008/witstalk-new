package top.xinsin.service;

import domain.AuthUserRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import top.xinsin.api.system.RemoteUserService;
import top.xinsin.api.system.domain.SysUser;
import top.xinsin.util.Result;

@Service
public class SysUserDetailsService {
    private final RemoteUserService remoteUserService;

    public SysUserDetailsService(RemoteUserService remoteUserService) {
        this.remoteUserService = remoteUserService;
    }

    public AuthUserRequest login(String username, String password) {
        Result<SysUser> userInfo = remoteUserService.getUserInfo(username);
        SysUser sysUser = userInfo.getData();
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        bCryptPasswordEncoder.matches(password, sysUser.getPassword());
        AuthUserRequest authUserRequest = new AuthUserRequest();
        authUserRequest.setUsername(sysUser.getUsername());
        authUserRequest.setPassword(sysUser.getPassword());
        authUserRequest.setNickName(sysUser.getNickName());
        authUserRequest.setId(sysUser.getId());
        return authUserRequest;
    }

    public void register(AuthUserRequest userDetails) {
        String encode = new BCryptPasswordEncoder().encode(userDetails.getPassword());
        Result<SysUser> sysUserResult = remoteUserService.register(userDetails.getUsername(), userDetails.getNickName(), encode);
        if (!sysUserResult.getCode().equals(200)) {
            throw new RuntimeException("注册失败: " + sysUserResult.getMsg());
        }
    }
}
