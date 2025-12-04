package cn.wzpmc.entities.system;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "sys_role", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysRole extends BaseEntity {
    private String roleName;
    private String roleKey;
    private String roleDesc;
    private String roleImg;
}
