package top.xinsin.controller;

import cn.wzpmc.entities.system.SysDictType;
import cn.wzpmc.entities.system.SysDictTypeItem;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import top.xinsin.service.impl.SysDictTypeItemServiceImpl;
import top.xinsin.service.impl.SysDictTypeServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

import java.util.List;

@RestController
@RequestMapping("/sysDictType")
public class SysDictTypeController {
    private final SysDictTypeServiceImpl sysDictTypeService;
    private final SysDictTypeItemServiceImpl sysDictTypeItemService;

    public SysDictTypeController(SysDictTypeServiceImpl sysDictTypeService, SysDictTypeItemServiceImpl sysDictTypeItemService) {
        this.sysDictTypeService = sysDictTypeService;
        this.sysDictTypeItemService = sysDictTypeItemService;
    }

    /**
     * 分页查询
     * @param sysDictType 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysDictType>> list(@RequestBody SysDictType sysDictType, Page<SysDictType> page) {
        return Result.success(sysDictTypeService.customPage(sysDictType, page));
    }

    /**
     * 添加
     * @param sysDictType 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysDictType sysDictType) {
        QueryWrapper eq = QueryWrapper.create()
                .eq(SysDictType::getDictType, sysDictType.getDictType());
        List<SysDictType> list = sysDictTypeService.list(eq);
        if (list.isEmpty()) {
            return Result.success(sysDictTypeService.save(sysDictType));
        } else {
            return Result.success(false);
        }
    }

    /**
     * 更新
     * @param sysDictType 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysDictType sysDictType) {
        return Result.success(sysDictTypeService.updateById(sysDictType));
    }

    /**
     * 删除
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysDictTypeService.removeById(id));
    }

    /**
     * 详情
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public Result<SysDictType> detail(@RequestParam("id") Long id) {
        return Result.success(sysDictTypeService.getById(id));
    }

    @PostMapping("/listByDictType")
    public Result<List<SysDictTypeItem>> listByDictType(@RequestParam("dictType") String dictType) {
        return Result.success(sysDictTypeService.listByDictType(dictType));
    }

    @GetMapping("/refreshCache")
    public Result<Boolean> refreshCache() {
        return Result.success(sysDictTypeService.refreshCache());
    }
}
