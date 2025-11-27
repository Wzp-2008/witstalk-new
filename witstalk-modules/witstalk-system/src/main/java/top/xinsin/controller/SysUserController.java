package top.xinsin.controller;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xinsin.api.system.RemoteUserService;
import top.xinsin.domain.SysUser;
import top.xinsin.entity.LoginUser;
import top.xinsin.service.impl.SysUserServiceImpl;
import top.xinsin.util.Result;
import top.xinsin.util.SecurityUtil;

import java.util.List;

@RestController
public class SysUserController {

    private final SysUserServiceImpl sysUserServiceImpl;

    public SysUserController(SysUserServiceImpl sysUserServiceImpl) {
        this.sysUserServiceImpl = sysUserServiceImpl;
    }

    @GetMapping("/getUserInfo")
    public Result<SysUser> getUserInfo(String username) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(SysUser::getUsername, username);
        return Result.success(sysUserServiceImpl.getOne(queryWrapper));
    }
    @GetMapping("/register")
    public Result<SysUser> register(@RequestParam("username") String username,@RequestParam("nickName") String nickName,@RequestParam("password") String password) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserServiceImpl.getOne(queryWrapper);
        if (sysUser != null) {
            return Result.fail("用户已存在");
        }
        SysUser sysUser1 = new SysUser();
        sysUser1.setUsername(username);
        sysUser1.setNickName(nickName);
        sysUser1.setPassword(password);
        sysUserServiceImpl.save(sysUser1);
        return Result.success();
    }

    @PostMapping("/debug")
    public Result<List<SysUser>> debug(@RequestBody SysUser sysUser){
//        LoginUser loginUser = SecurityUtil.getLoginUser();
        return Result.success(sysUserServiceImpl.list());
    }
    @PostMapping("/error")
    public Result<String> error(){
        return Result.success(JSON.toJSONString("{}"));
    }
}
