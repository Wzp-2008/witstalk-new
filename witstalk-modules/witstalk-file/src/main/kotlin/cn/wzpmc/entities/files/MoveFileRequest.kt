package cn.wzpmc.entities.files

import cn.wzpmc.entities.files.enums.FileType

data class MoveFileRequest(
    /**
     * 原始文件ID
     */
    var originalFileId: Long,

    /**
     * 原始文件类型
     */
    var fileType: FileType,

    /**
     * 新的父文件夹ID（可为空）
     */
    var newParentId: Long?,

    /**
     * 新的完整的文件名（包括扩展名）（可为空）
     */
    var newFilename: String?,
)