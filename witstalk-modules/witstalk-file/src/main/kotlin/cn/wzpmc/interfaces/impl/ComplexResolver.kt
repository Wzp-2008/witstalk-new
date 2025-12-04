package cn.wzpmc.interfaces.impl

import cn.wzpmc.entities.files.FullRawFileObject
import cn.wzpmc.mapper.FolderMapper
import cn.wzpmc.service.FileService
import cn.wzpmc.utils.log
import lombok.extern.log4j.Log4j2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.*

@Component
@Primary
@Log4j2
class ComplexResolver(
    folderMapper: FolderMapper,
    private val reverseResolver: ReverseResolver,
    private val simpleResolver: SimpleResolver
) : SimplePathResolver(folderMapper) {
    private lateinit var fileService: FileService

    @Autowired
    @Lazy
    fun setFileService(fileService: FileService) {
        this.fileService = fileService
    }
    override fun resolveFile(path: Array<String>): FullRawFileObject? {
        val strPath = path.contentToString()
        val targetFileName = path[path.size - 1]
        val lastDotIndex = targetFileName.lastIndexOf('.')
        var name = targetFileName
        var ext = ""
        if (lastDotIndex != -1) {
            name = targetFileName.take(lastDotIndex)
            ext = targetFileName.substring(lastDotIndex + 1)
        }
        val start = Date().time
        val totalRawFileCount: Long = this.fileService.getRawFilesCountByName(name, ext)
        if (totalRawFileCount == 0L) return null
        if (totalRawFileCount > path.size) {
            log.info("use simple resolver to solve path with {}", strPath)
            val rawFileObject: FullRawFileObject? = simpleResolver.resolveFile(path)
            val end = Date().time
            log.info("solve path {} cost {}ms", strPath, end - start)
            return rawFileObject
        }
        log.info("use reverse resolver to solve path with {}", strPath)
        val rawFileObject: FullRawFileObject? = reverseResolver.resolveFile(path)
        val end = Date().time
        log.info("solve path {} cost {}ms", strPath, end - start)
        return rawFileObject
    }
}