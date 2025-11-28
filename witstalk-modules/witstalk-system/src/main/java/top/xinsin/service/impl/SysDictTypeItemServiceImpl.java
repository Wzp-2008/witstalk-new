package top.xinsin.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xinsin.domain.SysDictTypeItem;
import top.xinsin.mapper.SysDictTypeItemMapper;
import top.xinsin.service.ISysDictTypeItemService;

@Service
public class SysDictTypeItemServiceImpl extends ServiceImpl<SysDictTypeItemMapper, SysDictTypeItem> implements ISysDictTypeItemService {
}
