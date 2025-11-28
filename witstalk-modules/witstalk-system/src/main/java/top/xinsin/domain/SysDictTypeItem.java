package top.xinsin.domain;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_dict_type_item")
public class SysDictTypeItem extends BaseEntity {
    private Integer dictTypeId;
    private String dictName;
    private String dictValue;
    private Integer sort;

}
