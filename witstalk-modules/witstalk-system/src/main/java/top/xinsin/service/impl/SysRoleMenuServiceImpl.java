package top.xinsin.service.impl;

import cn.wzpmc.entities.system.SysRoleMenu;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.mapper.SysRoleMenuMapper;
import top.xinsin.service.ISysRoleMenuService;
import top.xinsin.util.PageResult;

import java.util.ArrayList;

@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {
    public PageResult<SysRoleMenu> customPage(SysRoleMenu sysRoleMenu, Page<SysRoleMenu> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysRoleMenu);
        Page<SysRoleMenu> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords());
    }

    public boolean customSave(SysRoleMenu sysRoleMenu) {
        this.remove(QueryWrapper.create().eq(SysRoleMenu::getRoleId, sysRoleMenu.getRoleId()));
        ArrayList<SysRoleMenu> sysRoleMenus = new ArrayList<>();
        for (Long menuId : sysRoleMenu.getMenuIds()) {
            SysRoleMenu sysRoleMenu1 = new SysRoleMenu();
            sysRoleMenu1.setRoleId(sysRoleMenu.getRoleId());
            sysRoleMenu1.setMenuId(menuId);
            sysRoleMenus.add(sysRoleMenu1);
        }
        return this.saveBatch(sysRoleMenus);
    }
}
