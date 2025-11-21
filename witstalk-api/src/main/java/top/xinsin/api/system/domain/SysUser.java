package top.xinsin.api.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUser extends BaseEntity {
    private String username;
    private String password;
    private String nickName;
}
