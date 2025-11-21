package top.xinsin.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.domain.SysUser;
import top.xinsin.mapper.SysUserMapper;
import top.xinsin.service.ISysUserService;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
