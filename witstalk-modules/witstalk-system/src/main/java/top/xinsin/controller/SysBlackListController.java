package top.xinsin.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import cn.wzpmc.entities.system.SysBlackList;
import top.xinsin.service.impl.SysBlackListServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

import java.util.List;

@RestController
@RequestMapping("/sysBlackList")
public class SysBlackListController {
    private final SysBlackListServiceImpl sysBlackListService;

    public SysBlackListController(SysBlackListServiceImpl sysBlackListService) {
        this.sysBlackListService = sysBlackListService;
    }


    /**
     * 分页查询
     * @param sysBlackList 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysBlackList>> list(@RequestBody SysBlackList sysBlackList, Page<SysBlackList> page) {
        return Result.success(sysBlackListService.customPage(sysBlackList, page));
    }

    /**
     * 添加
     * @param sysBlackList 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysBlackList sysBlackList) {
        QueryWrapper eq = QueryWrapper.create()
                .eq(SysBlackList::getUserId, sysBlackList.getChannelId());
        List<SysBlackList> list = sysBlackListService.list(eq);
        if (list.isEmpty()) {
            return Result.success(sysBlackListService.save(sysBlackList));
        } else {
            return Result.success(false);
        }
    }

    /**
     * 更新
     * @param sysBlackList 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysBlackList sysBlackList) {
        return Result.success(sysBlackListService.updateById(sysBlackList));
    }

    /**
     * 删除
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysBlackListService.removeById(id));
    }

    /**
     * 详情
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public Result<SysBlackList> detail(@RequestParam("id") Long id) {
        return Result.success(sysBlackListService.getById(id));
    }
}
