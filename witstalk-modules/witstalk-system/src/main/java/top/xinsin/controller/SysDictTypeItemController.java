package top.xinsin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xinsin.service.impl.SysDictTypeItemServiceImpl;
import top.xinsin.service.impl.SysDictTypeServiceImpl;

@RestController
@RequestMapping("/sysDictTypeItem")
public class SysDictTypeItemController {
    private final SysDictTypeServiceImpl sysDictTypeService;
    private final SysDictTypeItemServiceImpl sysDictTypeItemService;

    public SysDictTypeItemController(SysDictTypeServiceImpl sysDictTypeService, SysDictTypeItemServiceImpl sysDictTypeItemService) {
        this.sysDictTypeService = sysDictTypeService;
        this.sysDictTypeItemService = sysDictTypeItemService;
    }
}
