package top.xinsin.service.impl;

import cn.wzpmc.entities.system.SysDictType;
import cn.wzpmc.entities.system.SysDictTypeItem;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.xinsin.mapper.SysDictTypeMapper;
import top.xinsin.service.ISysDictTypeService;
import top.xinsin.util.PageResult;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static top.xinsin.constants.CacheConstants.DICT_TYPE_KEY;

@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    private final SysDictTypeItemServiceImpl sysDictTypeItemService;
    private final RedisTemplate<String, Object> redisTemplate;

    public SysDictTypeServiceImpl(SysDictTypeItemServiceImpl sysDictTypeItemService, RedisTemplate<String, Object> redisTemplate) {
        this.sysDictTypeItemService = sysDictTypeItemService;
        this.redisTemplate = redisTemplate;
    }

    public PageResult<SysDictType> customPage(SysDictType sysDictType, Page<SysDictType> page) {
        QueryWrapper queryWrapper = QueryWrapper.create(sysDictType);
        Page<SysDictType> page1 = this.page(page, queryWrapper);
        return PageResult.page(page1.getPageNumber(), page1.getPageSize(), page1.getTotalPage(), page1.getRecords());
    }

    public List<SysDictTypeItem> listByDictType(String dictType) {
        Set<Object> members = redisTemplate.opsForSet().members(DICT_TYPE_KEY + dictType);
        if (members != null && !members.isEmpty()) {
            return members.stream().map(o -> (SysDictTypeItem) o).sorted(Comparator.comparingInt(SysDictTypeItem::getSort)).toList();
        }
        return null;
    }

    public Boolean refreshCache() {
        redisTemplate.keys(DICT_TYPE_KEY + "*").forEach(redisTemplate::delete);
        List<SysDictType> dictTypes = this.list();
        for (SysDictType dictType : dictTypes) {
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(SysDictTypeItem::getDictTypeId, dictType.getId());
            List<SysDictTypeItem> dictTypeItems = sysDictTypeItemService.list(queryWrapper);
            redisTemplate.opsForSet().add(DICT_TYPE_KEY + dictType.getDictType(), dictTypeItems.toArray(new Object[0]));
        }
        return null;
    }
}
