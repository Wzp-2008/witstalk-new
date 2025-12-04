package cn.wzpmc.entities.system;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "sys_user", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysUser extends BaseEntity {
    private String username;
    private String password;
    private String nickName;
    private String email;
    private String avatar;
}
