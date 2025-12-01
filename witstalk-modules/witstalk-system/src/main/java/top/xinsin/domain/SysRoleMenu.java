package top.xinsin.domain;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "sys_role_menu", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysRoleMenu extends BaseEntity {
    private Long roleId;
    private Long menuId;
}
