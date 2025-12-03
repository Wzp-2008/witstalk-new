package cn.wzpmc.entities.files.enums

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryMethods

enum class SortField(val column: QueryColumn) {
    /**
     * 通过ID排序（默认）
     */
    ID(QueryMethods.column("id")),

    /**
     * 通过文件夹排序
     */
    NAME(QueryMethods.column("name")),

    /**
     * 通过文件扩展名排序
     */
    EXT(QueryMethods.column("ext")),

    /**
     * 通过文件上传时间排序
     */
    TIME(QueryMethods.column("time")),

    /**
     * 通过文件上传者排序
     */
    UPLOADER(QueryMethods.column("owner")),

    /**
     * 通过文件下载次数排序
     */
    DOWNLOAD_COUNT(QueryMethods.column("down_count")),

    /**
     * 根据文件大小排序
     */
    SIZE(QueryMethods.column("size"));
}
