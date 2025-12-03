package cn.wzpmc.controller

import cn.wzpmc.entities.files.FilePathDescription
import cn.wzpmc.entities.files.FolderCreateRequest
import cn.wzpmc.entities.files.FullRawFileObject
import cn.wzpmc.entities.files.MoveFileRequest
import cn.wzpmc.entities.files.enums.FileType
import cn.wzpmc.entities.files.enums.SortField
import cn.wzpmc.entities.vo.FileVo
import cn.wzpmc.entities.vo.FolderVo
import cn.wzpmc.service.FileService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.xinsin.annotation.AuthorizationRequired
import top.xinsin.entity.LoginUser
import top.xinsin.util.PageResult
import top.xinsin.util.Result


/**
 * 文件操作相关接口
 */
@RestController
@RequestMapping("/api/file")
class FileController(private val fileService: FileService) {

    /**
     * 检查文件是否可以上传
     * @param description 文件路径
     * @return 是否可以上传
     */
    @PostMapping("/upload/check")
    fun checkUploadPossible(@RequestBody description: FilePathDescription): Result<Boolean> {
        return fileService.checkUploadPossible(description.name, description.folderId)
    }

    /**
     * 上传一个文件
     * @param file Multipart格式的文件对象
     * @return 文件详情
     */
    @PutMapping("/upload")
    fun simpleUpload(
        file: MultipartHttpServletRequest,
        @AuthorizationRequired user: LoginUser
    ): Result<FileVo> {
        return fileService.simpleUpload(file, user)
    }

    /**
     * 分页获取文件
     * @param page 要获取第几页的文件
     * @param num 每一页的文件数量
     * @param folder 要获取的文件所在的文件夹
     * @param sort 文件的排序方式
     * @param reverse 是否反向排序
     * @param keywords 搜索关键词
     * @return 分页后的文件列表
     */
    @GetMapping("/get")
    fun getFilePager(
        @RequestParam page: Long,
        @RequestParam num: Int,
        @RequestParam folder: Long,
        @RequestParam(defaultValue = "ID") sort: SortField,
        @RequestParam(defaultValue = "false") reverse: Boolean,
        @RequestParam(defaultValue = "") keywords: String
    ): Result<PageResult<FullRawFileObject>> {
        return fileService.getFilePager(page, num, folder, sort, reverse, keywords)
    }

    /**
     * 创建一个文件夹
     * @param request 创建文件夹的相关参数
     * @return 创建的文件夹详情
     */
    @PostMapping("/mkdir")
    fun mkdir(
        @RequestBody request: FolderCreateRequest,
        @AuthorizationRequired user: LoginUser
    ): Result<FolderVo?> {
        return fileService.mkdir(request, user)
    }

    /**
     * 获取一个文件的简略信息
     * @param id 文件ID
     * @return 文件简略信息
     */
    @GetMapping("/get/file")
    fun getFile(@RequestParam id: Long): Result<FullRawFileObject?> {
        return fileService.getFile(id)
    }

    /**
     * 获取一个文件夹的简略信息
     * @param id 文件夹ID
     * @return 文件夹简略信息
     */
    @GetMapping("/get/folder")
    fun getFolder(@RequestParam id: Long): Result<FullRawFileObject?> {
        return fileService.getFolder(id)
    }

    /**
     * 获取文件详细信息
     * @param id 文件ID
     * @return 文件详细信息
     */
    @GetMapping("/detail/file")
    fun getFileDetail(@RequestParam id: Long): Result<FileVo?> {
        return fileService.getFileDetail(id)
    }

    /**
     * 删除一个文件/文件夹
     * @param id 文件/文件夹ID
     * @param type 目标为文件/文件夹
     * @return 是否删除成功
     */
    @DeleteMapping("/rm")
    fun delete(
        @RequestParam id: Long,
        @RequestParam type: FileType,
        @AuthorizationRequired user: LoginUser,
    ): Result<Void?> {
        return fileService.delete(id, type, user)
    }

    /**
     * 获取文件下载链接
     * @param id 文件ID
     * @return 文件下载链接ID
     */
    @GetMapping("/link")
    fun getFileLink(@RequestParam id: Long): Result<String?> {
        return fileService.getFileLink(id)
    }

    /**
     * 通过下载文件ID下载文件
     * @param id 下载ID
     * @see .getFileLink
     */
    @GetMapping("/download/{id}")
    fun downloadFile(
        @PathVariable id: String,
        @RequestHeader(value = "Range", defaultValue = "null") range: String,
        response: HttpServletResponse
    ) {
        fileService.downloadFile(id, range, response)
    }

    /**
     * 通过路径解析文件信息
     * @param path 需要解析的文件路径
     * @return 文件粗略信息
     */
    @GetMapping("/path/resolve")
    fun resolveFileDetail(@RequestParam path: String): Result<FullRawFileObject?> {
        return fileService.resolveFileDetail(path)
    }

    /**
     * 通过文件ID获取文件路径
     * @param id 文件ID
     * @param type 目标文件为文件/文件夹
     * @return 文件的路径
     */
    @GetMapping("/path/{id}")
    fun findFilePathById(
        @PathVariable("id") id: Long,
        @RequestParam(value = "type", defaultValue = "FILE") type: FileType
    ): Result<String?> {
        return if (type == FileType.FILE) fileService.findFilePathById(id) else fileService.findFolderPathById(id)
    }


    /**
     * 移动文件位置
     * @param request 文件位置请求
     * @return 是否移动成功
     */
    @PatchMapping("/move")
    fun moveFile(@RequestBody request: MoveFileRequest, @AuthorizationRequired user: LoginUser): Result<Boolean> {
        return fileService.moveFile(request, user)
    }
}