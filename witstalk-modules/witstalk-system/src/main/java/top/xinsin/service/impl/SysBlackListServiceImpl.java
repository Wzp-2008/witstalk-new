package top.xinsin.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.wzpmc.entities.system.SysBlackList;
import top.xinsin.mapper.SysBlackListMapper;
import top.xinsin.service.ISysBlackListService;
import top.xinsin.util.PageResult;

@Service
public class SysBlackListServiceImpl extends ServiceImpl<SysBlackListMapper, SysBlackList> implements ISysBlackListService {
    public PageResult<SysBlackList> customPage(SysBlackList sysBlackList, Page<SysBlackList> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysBlackList);
        Page<SysBlackList> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords());
    }
}
