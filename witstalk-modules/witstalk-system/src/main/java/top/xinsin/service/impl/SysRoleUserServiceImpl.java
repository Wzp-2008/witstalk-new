package top.xinsin.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.domain.SysRoleMenu;
import top.xinsin.domain.SysRoleUser;
import top.xinsin.mapper.SysRoleMenuMapper;
import top.xinsin.mapper.SysRoleUserMapper;
import top.xinsin.service.ISysRoleMenuService;
import top.xinsin.service.ISysRoleUserService;

@Service
public class SysRoleUserServiceImpl extends ServiceImpl<SysRoleUserMapper, SysRoleUser> implements ISysRoleUserService {
}
