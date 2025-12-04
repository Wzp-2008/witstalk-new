package top.xinsin.controller;

import cn.wzpmc.entities.system.SysRoleUser;
import cn.wzpmc.entities.system.vo.SysUserAndRoleVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import top.xinsin.service.impl.SysRoleUserServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

import java.util.List;

@RestController
@RequestMapping("/sysRoleUser")
public class SysRoleUserController {
    private final SysRoleUserServiceImpl sysRoleUserService;

    public SysRoleUserController(SysRoleUserServiceImpl sysRoleUserService) {
        this.sysRoleUserService = sysRoleUserService;
    }

    /**
     * 分页查询
     * @param sysRoleUser 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysRoleUser>> list(@RequestBody SysRoleUser sysRoleUser, Page<SysRoleUser> page) {
        return Result.success(sysRoleUserService.customPage(sysRoleUser, page));
    }

    /**
     * 添加
     * @param sysRoleUser 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysRoleUser sysRoleUser) {
        QueryWrapper eq = QueryWrapper.create()
                .eq(SysRoleUser::getRoleId, sysRoleUser.getRoleId());
        List<SysRoleUser> list = sysRoleUserService.list(eq);
        if (list.isEmpty()) {
            return Result.success(sysRoleUserService.save(sysRoleUser));
        } else {
            return Result.success(false);
        }
    }

    /**
     * 更新
     * @param sysRoleUser 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysRoleUser sysRoleUser) {
        return Result.success(sysRoleUserService.updateById(sysRoleUser));
    }

    /**
     * 删除
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysRoleUserService.removeById(id));
    }

    /**
     * 详情
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public Result<SysRoleUser> detail(@RequestParam("id") Long id) {
        return Result.success(sysRoleUserService.getById(id));
    }

    /**
     * 保存接口
     * @param sysRoleUser 实体数据
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody SysRoleUser sysRoleUser) {
        return Result.success(sysRoleUserService.customSave(sysRoleUser));
    }

    @PostMapping("/getUserInfoByRoleId")
    public Result<List<SysUserAndRoleVO>> getUserInfoByRoleId(@RequestParam("roleId") Long roleId) {
        List<SysUserAndRoleVO> userAndRoleVOS = sysRoleUserService.getUserInfoByRoleId(roleId);
        return Result.success(userAndRoleVOS);
    }
}
