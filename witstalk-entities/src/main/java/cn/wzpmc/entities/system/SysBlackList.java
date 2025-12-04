package cn.wzpmc.entities.system;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xinsin.entity.BaseEntity;
import top.xinsin.listener.MyInsertListener;
import top.xinsin.listener.MyUpdateListener;

/**
 * 黑名单实体类
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "sys_black_list", onInsert = MyInsertListener.class, onUpdate = MyUpdateListener.class)
public class SysBlackList extends BaseEntity {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 频道ID
     */
    private Long channelId;
    /**
     * 频道名称
     */
    private String channelName;
    /**
     * 状态
     */
    private String status;
    /**
     * 状态ID
     */
    private Long statusId;
    /**
     * IP地址
     */
    private String ip;
}
