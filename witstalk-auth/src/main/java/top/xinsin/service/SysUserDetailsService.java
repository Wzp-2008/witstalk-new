package top.xinsin.service;

import cn.wzpmc.entities.system.SysUser;
import cn.wzpmc.entities.system.vo.SysUserAndAuthVO;
import top.xinsin.domain.AuthUserRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import top.xinsin.api.system.RemoteUserService;
import top.xinsin.util.Result;
import top.xinsin.util.SecurityUtil;

@Service
public class SysUserDetailsService {
    private final RemoteUserService remoteUserService;

    public SysUserDetailsService(RemoteUserService remoteUserService) {
        this.remoteUserService = remoteUserService;
    }

    public AuthUserRequest login(String username, String password) {
        Result<SysUserAndAuthVO> userInfo = remoteUserService.getUserInfo(username);
        SysUserAndAuthVO sysUser = userInfo.getData();
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(password, sysUser.getPassword());
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }
        AuthUserRequest authUserRequest = new AuthUserRequest();
        authUserRequest.setUsername(sysUser.getUsername());
        authUserRequest.setPassword(sysUser.getPassword());
        authUserRequest.setNickName(sysUser.getNickName());
        authUserRequest.setRoles(sysUser.getRoles());
        authUserRequest.setPermissions(sysUser.getPerms());
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

    public SysUserAndAuthVO userInfo() {
        SysUserAndAuthVO data = remoteUserService.getUserInfo(SecurityUtil.getLoginUser().getUsername()).getData();
        data.setPassword(null);
        return data;
    }
}
