package top.xinsin.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.domain.SysRole;
import top.xinsin.domain.SysRoleMenu;
import top.xinsin.mapper.SysRoleMapper;
import top.xinsin.mapper.SysRoleMenuMapper;
import top.xinsin.service.ISysRoleMenuService;
import top.xinsin.service.ISysRoleService;

@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {
}
