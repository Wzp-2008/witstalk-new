package cn.wzpmc.entities.vo

import com.mybatisflex.annotation.Table
import top.xinsin.entity.BaseEntity
import top.xinsin.listener.MyInsertListener
import top.xinsin.listener.MyUpdateListener

@Table("file_folder", onInsert = [MyInsertListener::class], onUpdate = [MyUpdateListener::class])
class FolderVo(
    /**
     * 文件夹名称
     */
    var name: String,
    /**
     * 文件夹的父文件夹ID
     */
    var parent: Long,
    /**
     * 文件夹创建者ID
     */
    var creator: Long,
): BaseEntity()