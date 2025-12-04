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
@Table(value = "sys_dict_type", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysDictType extends BaseEntity {
    private String dictType;
    private String dictName;
    private String dictDesc;

    @Column(ignore = true)
    private List<SysDictTypeItem> sysDictTypeItems;
}
