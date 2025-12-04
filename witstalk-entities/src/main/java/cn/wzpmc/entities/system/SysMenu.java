package cn.wzpmc.entities.system;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "sys_menu", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysMenu extends BaseEntity {
    private String menuName;
    private String menuPath;
    private String menuType;
    private String perms;
    private Long parentId;
}
