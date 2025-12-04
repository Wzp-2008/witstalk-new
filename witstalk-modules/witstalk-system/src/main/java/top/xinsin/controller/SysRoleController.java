package top.xinsin.controller;

import cn.wzpmc.entities.system.SysRole;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import top.xinsin.service.impl.SysRoleServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

import java.util.List;

@RestController
@RequestMapping("/sysRole")
public class SysRoleController {
    private final SysRoleServiceImpl sysRoleService;

    public SysRoleController(SysRoleServiceImpl sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /**
     * 分页查询
     * @param sysRole 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysRole>> list(@RequestBody SysRole sysRole, Page<SysRole> page) {
        return Result.success(sysRoleService.customPage(sysRole, page));
    }

    /**
     * 添加
     * @param sysRole 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysRole sysRole) {
        QueryWrapper eq = QueryWrapper.create()
                .eq(SysRole::getRoleKey, sysRole.getRoleKey());
        List<SysRole> list = sysRoleService.list(eq);
        if (list.isEmpty()) {
            return Result.success(sysRoleService.save(sysRole));
        } else {
            return Result.success(false);
        }
    }

    /**
     * 更新
     * @param sysRole 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysRole sysRole) {
        return Result.success(sysRoleService.updateById(sysRole));
    }

    /**
     * 删除
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysRoleService.removeById(id));
    }

    /**
     * 详情
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public Result<SysRole> detail(@RequestParam("id") Long id) {
        return Result.success(sysRoleService.getById(id));
    }
}
