package top.xinsin.service.impl;

import cn.wzpmc.entities.system.SysMenu;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.mapper.SysMenuMapper;
import top.xinsin.service.ISysMenuService;
import top.xinsin.util.PageResult;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {
    public PageResult<SysMenu> customPage(SysMenu sysMenu, Page<SysMenu> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysMenu);
        Page<SysMenu> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords());
    }
}
