package top.xinsin.service.impl;

import cn.wzpmc.entities.system.SysRoleUser;
import cn.wzpmc.entities.system.vo.SysUserAndRoleVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.mapper.SysRoleUserMapper;
import top.xinsin.service.ISysRoleUserService;
import top.xinsin.util.PageResult;

import java.util.ArrayList;
import java.util.List;

import static cn.wzpmc.entities.system.table.SysRoleUserTableDef.SYS_ROLE_USER;
import static cn.wzpmc.entities.system.table.SysUserTableDef.SYS_USER;

@Service
public class SysRoleUserServiceImpl extends ServiceImpl<SysRoleUserMapper, SysRoleUser> implements ISysRoleUserService {
    public PageResult<SysRoleUser> customPage(SysRoleUser sysRoleUser, Page<SysRoleUser> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysRoleUser);
        Page<SysRoleUser> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords());
    }

    public Boolean customSave(SysRoleUser sysRoleUser) {
        this.remove(QueryWrapper.create().eq(SysRoleUser::getRoleId, sysRoleUser.getRoleId()));
        ArrayList<SysRoleUser> sysRoleUsers = new ArrayList<>();
        for (Long menuId : sysRoleUser.getUserIds()) {
            SysRoleUser sysRoleMenu1 = new SysRoleUser();
            sysRoleMenu1.setRoleId(sysRoleUser.getRoleId());
            sysRoleMenu1.setUserId(menuId);
            sysRoleUsers.add(sysRoleMenu1);
        }
        return this.saveBatch(sysRoleUsers);
    }

    public List<SysUserAndRoleVO> getUserInfoByRoleId(Long roleId) {
        return this.queryChain()
                .select(
                        SYS_ROLE_USER.ALL_COLUMNS,
                        SYS_USER.USERNAME,
                        SYS_USER.NICK_NAME,
                        SYS_USER.EMAIL
                ).
                leftJoin(SYS_USER).on(SYS_ROLE_USER.USER_ID.eq(SYS_USER.ID))
                .where(SYS_ROLE_USER.ROLE_ID.eq(roleId))
                .listAs(SysUserAndRoleVO.class);
    }
}
