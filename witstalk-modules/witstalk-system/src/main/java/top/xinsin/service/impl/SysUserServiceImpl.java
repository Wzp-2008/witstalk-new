package top.xinsin.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.domain.SysDictTypeItem;
import top.xinsin.domain.SysUser;
import top.xinsin.mapper.SysUserMapper;
import top.xinsin.service.ISysUserService;
import top.xinsin.util.PageResult;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    public PageResult<SysUser> customPage(SysUser sysUser, Page<SysUser> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysUser);
        Page<SysUser> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords().stream().peek(item -> item.setPassword(null)).toList());
    }
}
