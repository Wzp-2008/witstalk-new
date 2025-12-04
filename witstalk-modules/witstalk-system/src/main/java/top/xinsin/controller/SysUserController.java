package top.xinsin.controller;

import cn.wzpmc.entities.system.SysUser;
import cn.wzpmc.entities.system.vo.SysUserAndAuthVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import top.xinsin.service.impl.SysUserServiceImpl;
import top.xinsin.util.PageResult;
import top.xinsin.util.Result;

import java.util.List;

@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    private final SysUserServiceImpl sysUserServiceImpl;

    public SysUserController(SysUserServiceImpl sysUserServiceImpl) {
        this.sysUserServiceImpl = sysUserServiceImpl;
    }

    /**
     * 分页查询
     * @param sysUser 实体类
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<SysUser>> list(@RequestBody SysUser sysUser, Page<SysUser> page) {
        return Result.success(sysUserServiceImpl.customPage(sysUser, page));
    }

    /**
     * 添加
     * @param sysUser 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SysUser sysUser) {
        QueryWrapper eq = QueryWrapper.create()
                .eq(SysUser::getUsername, sysUser.getUsername());
        List<SysUser> list = sysUserServiceImpl.list(eq);
        if (list.isEmpty()) {
            sysUser.setPassword(new BCryptPasswordEncoder().encode(sysUser.getPassword()));
            return Result.success(sysUserServiceImpl.save(sysUser));
        } else {
            return Result.success(false);
        }
    }

    /**
     * 更新
     * @param sysUser 字典类型实体类
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody SysUser sysUser) {
        return Result.success(sysUserServiceImpl.updateById(sysUser));
    }

    /**
     * 删除
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(sysUserServiceImpl.removeById(id));
    }

    /**
     * 详情
     * @param id 字典类型ID
     * @return 操作结果
     */
    @PostMapping("/detail")
    public Result<SysUser> detail(@RequestParam("id") Long id) {
        return Result.success(sysUserServiceImpl.getById(id));
    }

    @GetMapping("/getUserInfo")
    public Result<SysUserAndAuthVO> getUserInfo(String username) {
        return Result.success(sysUserServiceImpl.getUserInfoByUsername(username));
    }

    @GetMapping("/register")
    public Result<SysUser> register(@RequestParam("username") String username,@RequestParam("nickName") String nickName,@RequestParam("password") String password) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserServiceImpl.getOne(queryWrapper);
        if (sysUser != null) {
            return Result.fail("用户已存在");
        }
        SysUser sysUser1 = new SysUser();
        sysUser1.setUsername(username);
        sysUser1.setNickName(nickName);
        sysUser1.setPassword(password);
        sysUserServiceImpl.save(sysUser1);
        return Result.success();
    }
}
