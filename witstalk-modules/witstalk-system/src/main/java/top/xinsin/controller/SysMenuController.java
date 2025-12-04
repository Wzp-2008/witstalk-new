package top.xinsin.controller;

import cn.wzpmc.entities.system.SysMenu;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import top.xinsin.service.impl.SysMenuServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {
    private final SysMenuServiceImpl sysMenuService;

    public SysMenuController(SysMenuServiceImpl sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    /**
     * 分页查询
     * @param sysMenu 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysMenu>> list(@RequestBody SysMenu sysMenu, Page<SysMenu> page) {
        return Result.success(sysMenuService.customPage(sysMenu, page));
    }

    /**
     * 添加
     * @param sysMenu 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysMenu sysMenu) {
        QueryWrapper eq = QueryWrapper.create()
                .eq(SysMenu::getMenuName, sysMenu.getMenuName());
        List<SysMenu> list = sysMenuService.list(eq);
        if (list.isEmpty()) {
            return Result.success(sysMenuService.save(sysMenu));
        } else {
            return Result.success(false);
        }
    }

    /**
     * 更新
     * @param sysMenu 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysMenu sysMenu) {
        return Result.success(sysMenuService.updateById(sysMenu));
    }

    /**
     * 删除
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysMenuService.removeById(id));
    }

    /**
     * 删除多个
     * @param ids 字典类型IDs
     * @return 操作结果
     */
    @PostMapping("/remove")
    public Result<Boolean> remove(@RequestParam("ids") String ids) {
        String[] split = ids.split(",");
        return Result.success(sysMenuService.removeByIds(Arrays.asList(split)));
    }

    /**
     * 详情
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public Result<SysMenu> detail(@RequestParam("id") Long id) {
        return Result.success(sysMenuService.getById(id));
    }
}
