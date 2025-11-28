package top.xinsin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xinsin.service.impl.SysDictTypeItemServiceImpl;
import top.xinsin.service.impl.SysDictTypeServiceImpl;

@RestController
@RequestMapping("/sysDictType")
public class SysDictTypeController {
    private final SysDictTypeServiceImpl sysDictTypeService;
    private final SysDictTypeItemServiceImpl sysDictTypeItemService;

    public SysDictTypeController(SysDictTypeServiceImpl sysDictTypeService, SysDictTypeItemServiceImpl sysDictTypeItemService) {
        this.sysDictTypeService = sysDictTypeService;
        this.sysDictTypeItemService = sysDictTypeItemService;
    }

}
