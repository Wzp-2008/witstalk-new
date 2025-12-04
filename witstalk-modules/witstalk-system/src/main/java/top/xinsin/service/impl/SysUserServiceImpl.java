package top.xinsin.service.impl;

import cn.wzpmc.entities.system.SysUser;
import cn.wzpmc.entities.system.vo.SysUserAndAuthVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.xinsin.mapper.SysUserMapper;
import top.xinsin.service.ISysUserService;
import top.xinsin.util.PageResult;

import java.util.List;

import static cn.wzpmc.entities.system.table.SysMenuTableDef.SYS_MENU;
import static cn.wzpmc.entities.system.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static cn.wzpmc.entities.system.table.SysRoleTableDef.SYS_ROLE;
import static cn.wzpmc.entities.system.table.SysRoleUserTableDef.SYS_ROLE_USER;
import static cn.wzpmc.entities.system.table.SysUserTableDef.SYS_USER;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    public PageResult<SysUser> customPage(SysUser sysUser, Page<SysUser> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysUser);
        Page<SysUser> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords().stream().peek(item -> item.setPassword(null)).toList());
    }

    public SysUserAndAuthVO getUserInfoByUsername(String username) {
        SysUserAndAuthVO vo = this.queryChain()
                .select(
                        SYS_USER.ID,
                        SYS_USER.USERNAME,
                        SYS_USER.NICK_NAME,
                        SYS_USER.AVATAR,
                        SYS_USER.PASSWORD,
                        SYS_USER.EMAIL,
                        SYS_USER.CREATE_BY,
                        SYS_USER.CREATE_TIME,
                        SYS_USER.UPDATE_BY,
                        SYS_USER.UPDATE_TIME
                )
                .select(
                    "GROUP_CONCAT(DISTINCT sys_role.role_key ORDER BY sys_role.role_key) AS roleKeyArray",
                    "GROUP_CONCAT(DISTINCT sys_menu.perms ORDER BY sys_menu.perms) AS permArray"
                )
                // 内连接 + 前置过滤
                .innerJoin(SYS_ROLE_USER)
                .on(SYS_ROLE_USER.USER_ID.eq(SYS_USER.ID))
                .innerJoin(SYS_ROLE_MENU)
                .on(SYS_ROLE_MENU.ROLE_ID.eq(SYS_ROLE_USER.ROLE_ID))
                .leftJoin(SYS_MENU)
                .on(SYS_MENU.ID.eq(SYS_ROLE_MENU.MENU_ID))
                .innerJoin(SYS_ROLE)
                .on(SYS_ROLE.ID.eq(SYS_ROLE_MENU.ROLE_ID))
                // 主表过滤条件
                .where(SYS_USER.USERNAME.eq(username))
                // GROUP BY （按主表非聚合字段分组）
                .groupBy(
                        SYS_USER.ID,
                        SYS_USER.USERNAME,
                        SYS_USER.NICK_NAME,
                        SYS_USER.AVATAR,
                        SYS_USER.PASSWORD,
                        SYS_USER.EMAIL,
                        SYS_USER.CREATE_BY,
                        SYS_USER.CREATE_TIME,
                        SYS_USER.UPDATE_BY,
                        SYS_USER.UPDATE_TIME
                )
                // 关键：直接返回 SysUserAndAuthVO 类型（无数据返回 null）
                .oneAs(SysUserAndAuthVO.class);

        if (vo != null) {
            // 处理 roles：roleKeyArray 转 List<String>
            String roleKeyArray = vo.getRoleKeyArray();
            vo.setRoles(StringUtils.hasText(roleKeyArray) ?
                    List.of(roleKeyArray.split(",")) : List.of()); // 空字符串返回空列表

            // 处理 perms：permArray 转 List<String>
            String permArray = vo.getPermArray();
            vo.setPerms(StringUtils.hasText(permArray) ?
                    List.of(permArray.split(",")) : List.of());
        }

        return vo;
    }
}
