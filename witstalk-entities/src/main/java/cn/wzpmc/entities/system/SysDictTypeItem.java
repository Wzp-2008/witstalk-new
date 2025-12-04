package cn.wzpmc.entities.system;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "sys_dict_type_item", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysDictTypeItem extends BaseEntity {
    private Integer dictTypeId;
    private String dictName;
    private String dictValue;
    private Integer sort;

}
