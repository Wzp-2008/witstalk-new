package top.xinsin.controller;

import cn.wzpmc.entities.system.SysDictTypeItem;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.*;
import top.xinsin.service.impl.SysDictTypeItemServiceImpl;
import top.xinsin.service.impl.SysDictTypeServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

@RestController
@RequestMapping("/sysDictTypeItem")
public class SysDictTypeItemController {
    private final SysDictTypeServiceImpl sysDictTypeService;
    private final SysDictTypeItemServiceImpl sysDictTypeItemService;

    public SysDictTypeItemController(SysDictTypeServiceImpl sysDictTypeService, SysDictTypeItemServiceImpl sysDictTypeItemService) {
        this.sysDictTypeService = sysDictTypeService;
        this.sysDictTypeItemService = sysDictTypeItemService;
    }


    /**
     * 分页查询
     * @param sysDictTypeItem 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysDictTypeItem>> list(@RequestBody SysDictTypeItem sysDictTypeItem, Page<SysDictTypeItem> page) {
        return Result.success(sysDictTypeItemService.customPage(sysDictTypeItem, page));
    }

    /**
     * 添加
     * @param sysDictTypeItem 实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysDictTypeItem sysDictTypeItem) {
        return Result.success(sysDictTypeItemService.save(sysDictTypeItem));
    }

    /**
     * 更新
     * @param sysDictTypeItem 实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysDictTypeItem sysDictTypeItem) {
        return Result.success(sysDictTypeItemService.updateById(sysDictTypeItem));
    }

    /**
     * 删除
     * @param id 实体ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysDictTypeItemService.removeById(id));
    }

    /**
     * 详情
     * @param id 实体ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public  Result<SysDictTypeItem> detail(@RequestParam("id") Long id) {
        return Result.success(sysDictTypeItemService.getById(id));
    }
}
