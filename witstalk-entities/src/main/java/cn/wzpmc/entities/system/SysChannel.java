package cn.wzpmc.entities.system;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "sys_channel", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysChannel extends BaseEntity {
    /**
     * 父级ID
     */
    private Long parentId;
    /**
     * 频道名称
     */
    private String channelName;
    /**
     * 频道编码
     */
    private String channelCode;
    /**
     * 频道描述
     */
    private String channelDesc;
    /**
     * 频道提示
     */
    private String channelTip;
    /**
     * 频道图片
     */
    private String channelImg;
    /**
     * 频道类型
     */
    private String channelType;
    /**
     * 频道类型文本
     */
    private String channelTypeText;
    /**
     * 最大人数
     */
    private Long maxLength;
    /**
     * 权限ID
     */
    private Long permissionId;
    /**
     * 权限编码
     */
    private String permissionCode;
    /**
     * 是否密码保护
     */
    private Boolean isPassword;
    /**
     * 频道密码
     */
    private String password;
    /**
     * 是否公开
     */
    private Boolean isVisible;
    /**
     * 排序
     */
    private Long sort;
    /**
     * 是否置顶
     */
    private Boolean isTop;
}
