package cn.wzpmc.entities.vo

import com.mybatisflex.annotation.Table
import top.xinsin.entity.BaseEntity
import top.xinsin.listener.MyInsertListener
import top.xinsin.listener.MyUpdateListener

@Table("file_file",  onInsert = [MyInsertListener::class], onUpdate = [MyUpdateListener::class])
class FileVo(
    /**
     * 文件名称
     */
    var name: String,
    /**
     * 文件扩展名
     */
    var ext: String?,
    /**
     * 文件MIME类型
     */
    var mime: String,
    /**
     * 文件哈希值
     *
     * 哈希算法采用SHA512
     */
    var hash: String,
    /**
     * 文件上传者ID
     */
    var uploader: Long,
    /**
     * 文件夹ID
     */
    var folder: Long,
    /**
     * 文件大小
     *
     * 单位：字节（B）
     */
    var size: Long,
): BaseEntity()