package top.xinsin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xinsin.service.impl.SysRoleMenuServiceImpl;
import top.xinsin.service.impl.SysRoleServiceImpl;

@RestController
@RequestMapping("/sysRoleMenu")
public class SysRoleMenuController {
    private final SysRoleMenuServiceImpl sysRoleMenuService;

    public SysRoleMenuController(SysRoleMenuServiceImpl sysRoleMenuService) {
        this.sysRoleMenuService = sysRoleMenuService;
    }
}
