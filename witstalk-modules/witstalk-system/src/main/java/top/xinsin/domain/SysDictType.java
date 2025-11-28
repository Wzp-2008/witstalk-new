package top.xinsin.domain;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_dict_type")
public class SysDictType extends BaseEntity {
    private String dictType;
    private String dictName;
    private String dictDesc;

}
