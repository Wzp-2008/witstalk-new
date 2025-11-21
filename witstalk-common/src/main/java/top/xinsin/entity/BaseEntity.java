package top.xinsin.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

import java.util.Date;

@Data
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
    @Column(onInsertValue = "0")
    private String delFlag;
}
