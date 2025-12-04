package top.xinsin.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.wzpmc.entities.system.SysChannel;
import top.xinsin.mapper.SysChannelMapper;
import top.xinsin.service.ISysChannelService;
import top.xinsin.util.PageResult;

@Service
public class SysChannelServiceImpl extends ServiceImpl<SysChannelMapper, SysChannel> implements ISysChannelService {
    public PageResult<SysChannel> customPage(SysChannel sysChannel, Page<SysChannel> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysChannel);
        Page<SysChannel> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords());
    }
}
