package cn.wzpmc.entities.files

import cn.wzpmc.entities.files.enums.FileType
import java.util.Date

class FullRawFileObject(
    id: Long,
    name: String,
    ext: String? = null,
    size: Long,
    owner: Long,
    parent: Long,
    time: Date,
    type: FileType,
    mime: String,
    /**
     * 文件所有者名称
     */
    var ownerName: String,
    /**
     * 文件下载次数
     */
    var downCount: Long
) : RawFileObject(id, name, ext, size, owner, parent, time, type, mime) {
}
