package top.xinsin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xinsin.service.impl.SysRoleServiceImpl;
import top.xinsin.service.impl.SysRoleUserServiceImpl;

@RestController
@RequestMapping("/sysRoleUser")
public class SysRoleUserController {
    private final SysRoleUserServiceImpl sysRoleUserService;

    public SysRoleUserController(SysRoleUserServiceImpl sysRoleUserService) {
        this.sysRoleUserService = sysRoleUserService;
    }
}
