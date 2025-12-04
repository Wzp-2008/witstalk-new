package top.xinsin.service.impl;

import cn.wzpmc.entities.system.SysDictTypeItem;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.mapper.SysDictTypeItemMapper;
import top.xinsin.service.ISysDictTypeItemService;
import top.xinsin.util.PageResult;

@Service
public class SysDictTypeItemServiceImpl extends ServiceImpl<SysDictTypeItemMapper, SysDictTypeItem> implements ISysDictTypeItemService {
    public PageResult<SysDictTypeItem> customPage(SysDictTypeItem sysDictTypeItem, Page<SysDictTypeItem> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysDictTypeItem);
        Page<SysDictTypeItem> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords());
    }
}
