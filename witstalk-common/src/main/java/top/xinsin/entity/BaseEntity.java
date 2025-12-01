package top.xinsin.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

import java.util.Date;

@Data
@Table(value = "", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class BaseEntity {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String remark;
    private String createBy;
    @Column(onInsertValue = "now()")
    private Date createTime;
    private String updateBy;
    @Column(onUpdateValue = "now()")
    private Date updateTime;
    @Column(onInsertValue = "0", isLogicDelete = true)
    private String delFlag;
}
