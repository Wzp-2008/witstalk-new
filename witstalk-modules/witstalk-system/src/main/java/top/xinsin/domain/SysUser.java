package top.xinsin.domain;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_user")
public class SysUser extends BaseEntity {
    private String username;
    private String password;
    private String nickName;
}
