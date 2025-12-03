package cn.wzpmc.entities.files

import cn.wzpmc.entities.files.enums.FileType
import cn.wzpmc.entities.vo.FileVo
import cn.wzpmc.entities.vo.FolderVo
import java.util.*

open class RawFileObject(
    /**
     * 文件ID
     */
    var id: Long,

    /**
     * 文件名
     */
    var name: String,

    /**
     * 文件扩展名（文件夹为null）
     */
    var ext: String? = null,

    /**
     * 文件大小（文件夹为-1）
     */
    var size: Long,

    /**
     * 文件所有者
     */
    var owner: Long,

    /**
     * 父文件夹ID
     */
    var parent: Long,

    /**
     * 文件上传时间
     */
    var time: Date,

    /**
     * 文件类型
     */
    var type: FileType,

    /**
     * 文件mime类型
     */
    var mime: String
) {
    companion object {
        fun of(file: FileVo): RawFileObject {
            return RawFileObject(
                file.id,
                file.name,
                file.ext,
                file.size,
                file.uploader,
                file.folder,
                file.updateTime,
                FileType.FILE,
                file.mime
            )
        }

        fun of(folder: FolderVo): RawFileObject {
            return RawFileObject(
                folder.id,
                folder.name,
                null,
                -1,
                folder.creator,
                folder.parent,
                folder.createTime,
                FileType.FOLDER,
                "folder"
            )
        }

        fun getRawFileName(rawFileObject: RawFileObject): String? {
            return if (rawFileObject.type == FileType.FILE) rawFileObject.name + '.' + rawFileObject.ext else rawFileObject.name
        }
    }
}