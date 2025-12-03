package cn.wzpmc.entities.files

/**
 * 文件路径描述
 */
data class FilePathDescription(
    /**
     * 文件名
     */
    var name: String,
    /**
     * 目标文件夹ID
     */
    var folderId: Long
)