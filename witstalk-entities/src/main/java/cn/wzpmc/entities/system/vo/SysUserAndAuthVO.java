package cn.wzpmc.entities.system.vo;

import cn.wzpmc.entities.system.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserAndAuthVO extends SysUser {
    private List<String> roles;
    private List<String> perms;

    private String roleKeyArray;
    private String permArray;
}
