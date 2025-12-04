package cn.wzpmc.entities.system;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "sys_role_menu", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysRoleMenu extends BaseEntity {
    private Long roleId;
    private Long menuId;

    @Column(ignore = true)
    private List<Long> menuIds;
}
