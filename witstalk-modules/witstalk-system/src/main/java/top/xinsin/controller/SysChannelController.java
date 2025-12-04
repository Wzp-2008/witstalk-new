package top.xinsin.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import cn.wzpmc.entities.system.SysChannel;
import top.xinsin.service.impl.SysChannelServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

import java.util.List;

@RestController
@RequestMapping("/sysChannel")
public class SysChannelController {

    private final SysChannelServiceImpl sysChannelService;

    public SysChannelController(SysChannelServiceImpl sysChannelService) {
        this.sysChannelService = sysChannelService;
    }


    /**
     * 分页查询
     * @param sysChannel 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysChannel>> list(@RequestBody SysChannel sysChannel, Page<SysChannel> page) {
        return Result.success(sysChannelService.customPage(sysChannel, page));
    }

    /**
     * 添加
     * @param sysChannel 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysChannel sysChannel) {
        QueryWrapper eq = QueryWrapper.create()
                .eq(SysChannel::getChannelCode, sysChannel.getChannelCode());
        List<SysChannel> list = sysChannelService.list(eq);
        if (list.isEmpty()) {
            return Result.success(sysChannelService.save(sysChannel));
        } else {
            return Result.success(false);
        }
    }

    /**
     * 更新
     * @param sysChannel 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysChannel sysChannel) {
        return Result.success(sysChannelService.updateById(sysChannel));
    }

    /**
     * 删除
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysChannelService.removeById(id));
    }

    /**
     * 详情
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public Result<SysChannel> detail(@RequestParam("id") Long id) {
        return Result.success(sysChannelService.getById(id));
    }
}
