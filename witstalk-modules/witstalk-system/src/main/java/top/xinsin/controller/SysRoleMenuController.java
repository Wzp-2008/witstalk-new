package top.xinsin.controller;

import cn.wzpmc.entities.system.SysRoleMenu;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import top.xinsin.service.impl.SysRoleMenuServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

import java.util.List;

@RestController
@RequestMapping("/sysRoleMenu")
public class SysRoleMenuController {
    private final SysRoleMenuServiceImpl sysRoleMenuService;

    public SysRoleMenuController(SysRoleMenuServiceImpl sysRoleMenuService) {
        this.sysRoleMenuService = sysRoleMenuService;
    }

    /**
     * 分页查询
     * @param sysRoleMenu 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysRoleMenu>> list(@RequestBody SysRoleMenu sysRoleMenu, Page<SysRoleMenu> page) {
        return Result.success(sysRoleMenuService.customPage(sysRoleMenu, page));
    }

    /**
     * 添加
     * @param sysRoleMenu 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysRoleMenu sysRoleMenu) {
        QueryWrapper eq = QueryWrapper.create()
                .eq(SysRoleMenu::getRoleId, sysRoleMenu.getRoleId());
        List<SysRoleMenu> list = sysRoleMenuService.list(eq);
        if (list.isEmpty()) {
            return Result.success(sysRoleMenuService.save(sysRoleMenu));
        } else {
            return Result.success(false);
        }
    }

    /**
     * 更新
     * @param sysRoleMenu 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysRoleMenu sysRoleMenu) {
        return Result.success(sysRoleMenuService.updateById(sysRoleMenu));
    }

    /**
     * 删除
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysRoleMenuService.removeById(id));
    }

    /**
     * 详情
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public Result<SysRoleMenu> detail(@RequestParam("id") Long id) {
        return Result.success(sysRoleMenuService.getById(id));
    }

    /**
     * 保存接口
     * @param sysRoleMenu 实体数据
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody SysRoleMenu sysRoleMenu) {
        return Result.success(sysRoleMenuService.customSave(sysRoleMenu));
    }
}
