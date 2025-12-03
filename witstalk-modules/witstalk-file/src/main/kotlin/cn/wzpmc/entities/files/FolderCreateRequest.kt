package cn.wzpmc.entities.files

data class FolderCreateRequest(
    /**
     * 父文件夹ID
     */
    var parent: Long,
    /**
     * 文件名
     */
    var name: String,
    /**
     * 当文件夹存在时返回已存在的文件夹
     */
    var existsReturn: Boolean,
)