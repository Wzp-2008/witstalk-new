package top.xinsin.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.domain.SysMenu;
import top.xinsin.domain.SysRole;
import top.xinsin.mapper.SysMenuMapper;
import top.xinsin.mapper.SysRoleMapper;
import top.xinsin.service.ISysMenuService;
import top.xinsin.service.ISysRoleService;
import top.xinsin.util.PageResult;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    public PageResult<SysRole> customPage(SysRole sysRole, Page<SysRole> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysRole);
        Page<SysRole> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords());
    }
}
