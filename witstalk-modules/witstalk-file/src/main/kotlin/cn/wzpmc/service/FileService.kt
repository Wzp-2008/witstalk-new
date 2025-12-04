package cn.wzpmc.service

import cn.wzpmc.entities.files.FolderCreateRequest
import cn.wzpmc.entities.files.FullRawFileObject
import cn.wzpmc.entities.files.MoveFileRequest
import cn.wzpmc.entities.files.RawFileObject
import cn.wzpmc.entities.files.enums.FileType
import cn.wzpmc.entities.files.enums.SortField
import cn.wzpmc.entities.vo.FileVo
import cn.wzpmc.entities.vo.FolderVo
import cn.wzpmc.entities.vo.table.FileVoTableDef.FILE_VO
import cn.wzpmc.entities.vo.table.FolderVoTableDef.FOLDER_VO
import cn.wzpmc.interfaces.FilePathService
import cn.wzpmc.mapper.FileMapper
import cn.wzpmc.mapper.FolderMapper
import cn.wzpmc.properties.FileManagerProperties
import cn.wzpmc.utils.RandomUtils
import cn.wzpmc.utils.log
import cn.wzpmc.utils.stream.SizeStatisticsDigestInputStream
import com.mybatisflex.core.audit.http.HashUtil
import com.mybatisflex.core.query.QueryMethods
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.update.UpdateChain
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.HttpServletResponse
import lombok.SneakyThrows
import lombok.extern.slf4j.Slf4j
import org.apache.commons.codec.digest.DigestUtils
import org.apache.tika.Tika
import org.apache.tomcat.util.http.fileupload.FileUpload
import org.apache.tomcat.util.http.fileupload.impl.FileItemIteratorImpl
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StreamUtils
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.xinsin.domain.table.SysUserTableDef.SYS_USER
import top.xinsin.entity.LoginUser
import top.xinsin.util.PageResult
import top.xinsin.util.Result
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

@Slf4j
@Service
class FileService(
    private val fileMapper: FileMapper,
    private val folderMapper: FolderMapper,
    private val randomUtils: RandomUtils,
    private val properties: FileManagerProperties,
    private val redisTemplate: RedisTemplate<String, FileVo>,
    private val idAddrLinkMapper: StringRedisTemplate,
    private val savePath: File,
) {
    private lateinit var pathService: FilePathService
    @Autowired
    @Lazy
    fun setPathService(pathService: FilePathService) {
        this.pathService = pathService
    }

    private fun tryDeleteOrDeleteOnExit(tmpFile: File) {
        if (!tmpFile.delete()) {
            log.error("delete tmp file error")
            tmpFile.deleteOnExit()
        }
    }

    private fun getFilename(name: String): FilenameDescription {
        val i = name.lastIndexOf(".")
        var extName: String? = null
        var start = name
        if (i != -1) {
            start = name.take(i)
        }
        if (!(i == -1 || i == name.length - 1)) {
            extName = name.substring(i + 1)
        }
        return FilenameDescription(start, extName)
    }

    private fun <T> checkFilenameConflict(filename: FilenameDescription, folder: Long?): Optional<Result<T>> {
        return checkFilenameConflict(filename.name, filename.ext, folder)
    }

    private fun <T> checkFilenameConflict(start: String?, extName: String?, folder: Long?): Optional<Result<T>> {
        if (fileMapper.selectCountByCondition(
                FILE_VO.NAME.eq(start).and(FILE_VO.EXT.eq(extName)).and(FILE_VO.FOLDER.eq(folder))
            ) > 0
        ) {
            return Optional.of(Result.fail(HttpStatus.CONFLICT, "存在同名文件，请改名或删除后重试！"))
        }
        return Optional.empty()
    }

    @SneakyThrows
    @Transactional
    fun simpleUpload(request: MultipartHttpServletRequest, user: LoginUser): Result<FileVo> {
        val folderParams: Long = getFolderParams(request)
        val servletRequestContext = ServletRequestContext(request)
        val upload = FileUpload()
        val fileItemIterator = FileItemIteratorImpl(upload, servletRequestContext)
        var lastUploadFile: FileVo? = null
        while (fileItemIterator.hasNext()) {
            val next = fileItemIterator.next()
            val fieldName = next.fieldName
            if (fieldName == "file") {
                val name = next.name
                val filename = getFilename(name)
                val illegalResult = filename.checkIllegal<FileVo>()
                if (illegalResult.isPresent) {
                    return illegalResult.get()
                }
                val start = filename.name
                val extName = filename.ext
                val inputStream = next.openStream()
                val digestInputStream =
                    SizeStatisticsDigestInputStream(inputStream, DigestUtils.getSha512Digest())
                val tmpFile = File(savePath, "cache-" + randomUtils.generatorRandomFileName(20))
                try {
                    FileOutputStream(tmpFile).use<FileOutputStream, Unit> { fileOutputStream ->
                        StreamUtils.copy(digestInputStream, fileOutputStream)
                    }
                    digestInputStream.close()
                    val hex = HashUtil.toHex(digestInputStream.messageDigest.digest())
                    val size: Long = digestInputStream.size
                    if (size == 0L) {
                        tryDeleteOrDeleteOnExit(tmpFile)
                        return Result.fail(HttpStatus.LENGTH_REQUIRED, "请勿上传空文件！")
                    }
                    val tika = Tika()
                    val detect: String = tika.detect(tmpFile)
                    val fileVo = FileVo(
                        uploader = user.userId,
                        mime = detect,
                        size = size,
                        name = start,
                        ext = extName,
                        hash = hex,
                        folder = folderParams
                    )
                    fileMapper.insert(fileVo)
                    val targetFile = File(savePath, hex)
                    lastUploadFile = fileVo
                    if (targetFile.isFile()) {
                        tryDeleteOrDeleteOnExit(tmpFile)
                        continue
                    }
                    if (!tmpFile.renameTo(targetFile)) {
                        throw RuntimeException("error while moving file")
                    }
                } catch (e: Exception) {
                    log.error("error while processing file", e)
                } finally {
                    tryDeleteOrDeleteOnExit(tmpFile)
                }
            }
        }
        if (lastUploadFile == null) {
            return Result.fail(HttpStatus.BAD_REQUEST, "未找到文件参数")
        }
        return Result.success("上传成功！", lastUploadFile)
    }

    private val rawFileSelector: QueryWrapper
        get() {
            return QueryMethods.select(
                FILE_VO.ID,
                FILE_VO.NAME,
                FILE_VO.EXT,
                FILE_VO.SIZE,
                FILE_VO.FOLDER.`as`("parent"),
                FILE_VO.UPLOADER.`as`("owner"),
                FILE_VO.CREATE_TIME.`as`("time"),
                QueryMethods.string("FILE").`as`("type"),
                FILE_VO.MIME.`as`("mime"),
                SYS_USER.USERNAME.`as`("owner_name")
            ).from(FILE_VO)
            .leftJoin<QueryWrapper>(SYS_USER).on(SYS_USER.ID.eq(FILE_VO.UPLOADER))
                .groupBy(FILE_VO.ID)
        }

    private val rawFolderSelector: QueryWrapper
        get() = QueryMethods.select(
            FOLDER_VO.ID,
            FOLDER_VO.NAME,
            QueryMethods.null_().`as`("ext"),
            QueryMethods.number(-1).`as`("size"),
            FOLDER_VO.PARENT,
            FOLDER_VO.CREATOR.`as`("owner"),
            FOLDER_VO.CREATE_TIME.`as`("time"),
            QueryMethods.string("FOLDER").`as`("type"),
            QueryMethods.string("folder").`as`("mime"),
            SYS_USER.USERNAME.`as`("owner_name"),
            QueryMethods.number(0).`as`("down_count")
        ).from(FOLDER_VO)
            .leftJoin<QueryWrapper>(SYS_USER).on(SYS_USER.ID.eq(FOLDER_VO.CREATOR))

    fun getFilePager(
        page: Long,
        num: Int,
        folder: Long,
        sort: SortField,
        reverse: Boolean,
        keywords: String
    ): Result<PageResult<FullRawFileObject>> {
        var rawFileSelect = this.rawFileSelector
        var rawFolderSelect = this.rawFolderSelector
        var queryFolder = true
        if (keywords.isEmpty()) {
            rawFileSelect = rawFileSelect.where(FILE_VO.FOLDER.eq(folder))
            rawFolderSelect = rawFolderSelect.where(FOLDER_VO.PARENT.eq(folder))
        } else {
            val filename = getFilename(keywords)
            if (filename.ext.isEmpty()) {
                rawFileSelect = rawFileSelect.where(
                    FILE_VO.NAME.like("%$keywords%").or(FILE_VO.EXT.like("%$keywords%"))
                )
                rawFolderSelect.where(FOLDER_VO.NAME.like("%$keywords%"))
            } else {
                queryFolder = false
                rawFileSelect = rawFileSelect.where(
                    FILE_VO.NAME.like("%" + filename.name + "%").and(FILE_VO.EXT.like("%" + filename.ext + "%"))
                )
            }
        }
        if (queryFolder) {
            rawFileSelect = rawFileSelect.unionAll(rawFolderSelect)
        }
        var from: QueryWrapper =
            QueryWrapper().with<QueryWrapper>("RAW_FILE").asSelect(rawFileSelect).select().from("RAW_FILE")
        if (sort !== SortField.ID) {
            from = from.orderBy(sort.column, reverse)
        }
        from = from.orderBy(QueryMethods.column("id"), reverse)
        val size = fileMapper.selectCountByQuery(QueryMethods.selectCount().from(rawFileSelect).`as`("subQuery"))
        val paginate =
            fileMapper.paginateAs(page, num, size, from, FullRawFileObject::class.java)
        val result = PageResult.page(page, num.toLong(),paginate.totalRow, paginate.getRecords())
        return Result.success(result)
    }


    fun mkdir(request: FolderCreateRequest, user: LoginUser): Result<FolderVo?> {
        val name: String = request.name
        val parent: Long = request.parent
        if (name.isEmpty()) {
            return Result.fail(HttpStatus.BAD_REQUEST, "文件名不可为空！")
        }
        if (name.length > 160) {
            return Result.fail(HttpStatus.PAYLOAD_TOO_LARGE, "文件夹名称过长，无法创建！")
        }
        if (fileMapper.selectCountByCondition(
                FILE_VO.EXT.eq(QueryMethods.null_()).and(FILE_VO.NAME.eq(name)).and(FILE_VO.FOLDER.eq(parent))
            ) > 0
        ) {
            return Result.fail(HttpStatus.CONFLICT, "创建文件夹失败，同名文件已存在！")
        }
        val folder: FolderVo? =
            folderMapper.selectOneByCondition(FOLDER_VO.NAME.eq(name).and(FOLDER_VO.PARENT.eq(parent)))
        if (folder != null) {
            if (request.existsReturn) {
                return Result.success("文件夹已存在！", folder)
            }
            return Result.fail(HttpStatus.CONFLICT, "创建文件夹失败，同名文件夹已存在！")
        }
        val folderVo = FolderVo(
            name = name,
            creator =  user.userId,
            parent = parent
        )
        folderMapper.insert(folderVo)
        return Result.success("创建成功", folderVo)
    }

    private fun deleteFile(fileVo: FileVo) {
        if (fileMapper.selectCountByCondition(FILE_VO.HASH.eq(fileVo.hash)) <= 0) {
            val hash = fileVo.hash
            val file = File(properties.savePath, hash)
            if (file.exists()) {
                if (!file.delete()) {
                    log.error("删除文件 {} 失败！", file)
                }
            } else {
                log.error("存储目录可能损坏，在删除文件 {} 时未发现文件", file)
            }
        }
    }

    fun deleteFolder(id: Long) {
        fileMapper.selectListByCondition(FILE_VO.FOLDER.eq(id)).forEach { fileVo: FileVo -> this.deleteFile(fileVo) }
        fileMapper.deleteByCondition(FILE_VO.FOLDER.eq(id))
        folderMapper.selectListByCondition(FOLDER_VO.PARENT.eq(id)).stream().map(FolderVo::getId)
            .forEach { id: Long -> this.deleteFolder(id) }
        folderMapper.deleteById(id)
    }

    @Transactional
    fun delete(id: Long, type: FileType, user: LoginUser): Result<Void?> {
        val actorId = user.userId
        if (type == FileType.FILE) {
            val fileVo: FileVo = fileMapper.selectOneById(id) ?: return Result.fail(HttpStatus.NOT_FOUND, "文件不存在！")
            if (fileMapper.selectCountByCondition(FILE_VO.ID.eq(id).and(FILE_VO.UPLOADER.eq(actorId))) <= 0) {
                return Result.fail(HttpStatus.UNAUTHORIZED, "权限不足！")
            }
            /* if (user.getAuth().equals(Auth.user)) {

            } */
            fileMapper.deleteById(fileVo.id)
            deleteFile(fileVo)
        } else {
            return Result.fail(HttpStatus.UNAUTHORIZED, "权限不足！")
            /* val folder: FolderVo =
                folderMapper.selectOneById(id) ?: return Result.fail(HttpStatus.NOT_FOUND, "文件不存在！") */
            // this.deleteFolder(folder.id)
        }
        return Result.success()
    }

    fun downloadFile(id: String, range: String, response: HttpServletResponse) {
        val fileVo: FileVo? = redisTemplate.opsForValue().get(id)?.let<Any, FileVo?> { vo ->
            vo as? FileVo
        }
        if (fileVo == null) {
            Result.fail<Unit>(HttpStatus.NOT_FOUND, "未知文件！").writeToResponse(response)
            return
        }
        val size = fileVo.size
        val hash = fileVo.hash
        val file = File(properties.savePath(), hash)
        var min: Long = 0
        var max = size - 1
        if (range != "null") {
            val unitRanges = range.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val minMax = unitRanges[1].split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (minMax.isNotEmpty()) {
                min = minMax[0].toLong()
            }
            if (minMax.size > 1) {
                max = minMax[1].toLong()
            }
            response.status = 206
            response.addHeader("Content-Range", "bytes $min-$max/$size")
            response.addHeader("Accept-Ranges", "bytes")
        } else {
            response.status = 200
        }
        log.debug("-------Prepare-Response-{}-{}-{}-------", min, max, id)
        var fullName = fileVo.name
        val ext = fileVo.ext
        if (ext != null) {
            fullName += ".$ext"
        }
        response.addHeader("Content-Length", (max - min + 1).toString())
        val disposition: ContentDisposition =
            ContentDisposition.attachment().filename(fullName, StandardCharsets.UTF_8).build()
        response.addHeader("Content-Disposition", disposition.toString())

        log.debug("-------Copy-{}-{}-{}-------", min, max, id)
        try {
            response.outputStream.use<ServletOutputStream, Unit> { outputStream ->
                FileInputStream(file).use<FileInputStream, Unit> { stream ->
                    StreamUtils.copyRange(stream, outputStream, min, max)
                }
            }
        } catch (_: IOException) {
            if (!response.isCommitted) {
                response.reset()
            }
        }
        log.debug("-------flush-{}-{}-{}-------", min, max, id)
    }

    fun getFileLink(id: Long): Result<String?> {
        val fileVo = fileMapper.selectOneById(id) ?: return Result.fail(HttpStatus.NOT_FOUND, "未知ID文件")
        val fileId = fileVo.id
        val identify: String = ID_ADDR_PREFIX + fileId
        var link = idAddrLinkMapper.opsForValue().get(identify)
        if (link == null) {
            link = randomUtils.generatorRandomFileName(8)
            redisTemplate.opsForValue().set(link, fileVo, 30, TimeUnit.MINUTES)
            idAddrLinkMapper.opsForValue().set(identify, link, 30, TimeUnit.MINUTES)
        }
        return Result.success("成功", link)
    }

    fun resolveFileDetail(path: String): Result<FullRawFileObject?> {
        val split: Array<String> = path.split(PATH_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val fileObj: FullRawFileObject =
            pathService.resolveFile(split) ?: return Result.fail(HttpStatus.NOT_FOUND, "文件不存在！")
        return Result.success(fileObj)
    }

    fun findFilePathById(id: Long): Result<String?> {
        val fileVo: FileVo = fileMapper.selectOneById(id) ?: return Result.fail(HttpStatus.NOT_FOUND, "文件不存在！")
        return Result.success("成功", pathService.getFilePath(RawFileObject.of(fileVo)))
    }

    fun findFolderPathById(id: Long): Result<String?> {
        val folderVo: FolderVo =
            folderMapper.selectOneById(id) ?: return Result.fail(HttpStatus.NOT_FOUND, "文件夹不存在")
        return Result.success("成功", pathService.getFilePath(RawFileObject.of(folderVo)))
    }

    fun getFile(id: Long): Result<FullRawFileObject?> {
        val fileVo: FullRawFileObject? = this.fileMapper.selectOneByQueryAs(
            this.rawFileSelector.where(FILE_VO.ID.eq(id)),
            FullRawFileObject::class.java
        )
        if (fileVo == null) {
            return Result.fail(HttpStatus.NOT_FOUND, "未知文件")
        }
        return Result.success(fileVo)
    }

    fun getFolder(id: Long): Result<FullRawFileObject?> {
        val fileVo: FullRawFileObject? = this.folderMapper.selectOneByQueryAs(
            this.rawFolderSelector.where(FOLDER_VO.ID.eq(id)),
            FullRawFileObject::class.java
        )
        if (fileVo == null) {
            return Result.fail(HttpStatus.NOT_FOUND, "未知文件")
        }
        return Result.success(fileVo)
    }

    fun getFileDetail(id: Long): Result<FileVo?> {
        val fileVo: FileVo = this.fileMapper.selectOneById(id) ?: return Result.fail(HttpStatus.NOT_FOUND, "未知文件")
        return Result.success(fileVo)
    }
    fun checkUploadPossible(name: String, folder: Long?): Result<Boolean> {
        val filename = getFilename(name)
        val illegalResult = filename.checkIllegal<Boolean>()
        if (illegalResult.isPresent) {
            return illegalResult.get()
        }
        val objectResult = this.checkFilenameConflict<Boolean>(filename, folder)
        return objectResult.orElseGet(Supplier { Result.success("可以上传", true) })
    }

    fun getRawFilesByNameAndFolder(name: String, ext: String?, folder: Long): MutableList<FullRawFileObject> {
        return fileMapper.selectListByQueryAs(
            this.rawFileSelector.where(
                FILE_VO.NAME.eq(name).and(SortField.EXT.column.eq(ext)).and(FILE_VO.FOLDER.eq(folder))
            )
                .unionAll(this.rawFolderSelector.where(FOLDER_VO.NAME.eq(name).and(FOLDER_VO.PARENT.eq(folder)))),
            FullRawFileObject::class.java
        )
    }

    private fun getRawFilesByNameQuery(name: String, ext: String?): QueryWrapper {
        return this.rawFileSelector.where(FILE_VO.NAME.eq(name).and(SortField.EXT.column.eq(ext)))
            .unionAll(this.rawFolderSelector.where(FOLDER_VO.NAME.eq(name)))
    }

    fun getRawFilesByName(name: String, ext: String?): MutableList<FullRawFileObject> {
        return fileMapper.selectListByQueryAs(this.getRawFilesByNameQuery(name, ext), FullRawFileObject::class.java)
    }

    fun getRawFilesCountByName(name: String, ext: String?): Long {
        return fileMapper.selectCountByQuery(
            QueryMethods.selectCount().from(this.getRawFilesByNameQuery(name, ext)).`as`("subQuery")
        )
    }

    fun moveFile(request: MoveFileRequest, user: LoginUser): Result<Boolean> {
        val originalFileId = request.originalFileId
        val fileType = request.fileType
        val userId: Long = user.userId
        val isAdmin = false// user.getAuth().equals(Auth.admin)
        if (fileType == FileType.FILE) {
            val fileVo: FileVo =
                fileMapper.selectOneById(originalFileId) ?: return Result.fail(HttpStatus.NOT_FOUND, "原始文件不存在！")
            if (!isAdmin && fileVo.uploader != userId) {
                return Result.fail(HttpStatus.FORBIDDEN, "权限不足！")
            }
            var newFilename = request.newFilename
            if (newFilename == null) {
                val ext = fileVo.ext
                newFilename = fileVo.name + (if (ext == null) "" else ".$ext")
            }
            var newParentId = request.newParentId
            if (newParentId == null) {
                newParentId = fileVo.folder
            }
            if (fileMapper.selectCountByCondition(
                    FILE_VO.NAME.eq(newFilename).and(FILE_VO.FOLDER.eq(newParentId))
                ) > 0
            ) {
                return Result.fail(HttpStatus.CONFLICT, "文件已存在！")
            }
            UpdateChain.of(FILE_VO)
                .set(FILE_VO.NAME, newFilename)
                .set(FILE_VO.FOLDER, newParentId)
                .where { FILE_VO.ID.eq(originalFileId) }
                .update()
            return Result.success(true)
        }
        val folderVo: FolderVo =
            folderMapper.selectOneById(originalFileId) ?: return Result.fail(HttpStatus.NOT_FOUND, "原始文件不存在！")
        if (!isAdmin) {
            return Result.fail(HttpStatus.FORBIDDEN, "权限不足！")
        }
        var newFilename = request.newFilename
        if (newFilename == null) {
            newFilename = folderVo.name
        }
        var newParentId = request.newParentId
        if (newParentId == null) {
            newParentId = folderVo.parent
        }
        if (folderMapper.selectCountByCondition(
                FOLDER_VO.NAME.eq(newFilename).and(FOLDER_VO.PARENT.eq(newParentId))
            ) > 0
        ) {
            return Result.fail(HttpStatus.CONFLICT, "文件已存在！")
        }
        UpdateChain.of(FOLDER_VO)
            .set(FOLDER_VO.NAME, newFilename)
            .set(FOLDER_VO.PARENT, newParentId)
            .where { FOLDER_VO.ID.eq(originalFileId) }
            .update()
        return Result.success(true)
    }

    private class FilenameDescription(val name: String, ext: String?) {
        val ext: String
        init {
            if (ext == null) {
                this.ext = ""
            } else {
                this.ext = ext
            }
        }
        fun <T> checkIllegal(): Optional<Result<T>> {
            if (name.length > 120 || ext.length > 40) {
                return Optional.of(Result.fail(HttpStatus.PAYLOAD_TOO_LARGE, "文件名过长，无法上传！"))
            }
            if (name.isEmpty()) {
                return Optional.of(Result.fail(HttpStatus.BAD_REQUEST, "文件名为空，无法上传！"))
            }
            return Optional.empty()
        }
    }

    companion object {
        const val ID_ADDR_PREFIX: String = "ID_ADDR_"
        const val PATH_SEPARATOR_CHAR: Char = '/'
        const val PATH_SEPARATOR: String = "$PATH_SEPARATOR_CHAR"

        private fun getFolderParams(request: MultipartHttpServletRequest): Long {
            val params: String? = request.queryString
            var folderParams = -1L
            if (params != null && !params.isEmpty()) {
                val param = params.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (s in param) {
                    val keyValue = s.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (keyValue.size == 2) {
                        val key = keyValue[0]
                        val value = keyValue[1]
                        if (key == "folder") {
                            folderParams = value.toLong()
                        }
                    }
                }
            }
            return folderParams
        }
    }
}