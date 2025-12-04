package cn.wzpmc.interfaces.impl

import cn.wzpmc.entities.files.FullRawFileObject
import cn.wzpmc.entities.files.enums.FileType
import cn.wzpmc.entities.vo.FolderVo
import cn.wzpmc.entities.vo.table.FolderVoTableDef.FOLDER_VO
import cn.wzpmc.mapper.FolderMapper
import cn.wzpmc.service.FileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Lazy

@Component
class SimpleResolver(folderMapper: FolderMapper) : SimplePathResolver(folderMapper) {
    private lateinit var fileService: FileService

    @Autowired
    @Lazy
    fun setFileService(fileService: FileService) {
        this.fileService = fileService
    }

    override fun resolveFile(path: Array<String>): FullRawFileObject? {
        return resolveFile(removeEmptyPath(path), -1)
    }

    private fun resolveFile(
        path: MutableList<String>,
        parentId: Long
    ): FullRawFileObject? {
        val currentLayerName = path[0]
        if (path.size == 1) {
            val lastDotIndex = currentLayerName.lastIndexOf('.')
            var name = currentLayerName
            var ext = ""
            if (lastDotIndex != -1) {
                name = currentLayerName.take(lastDotIndex)
                ext = currentLayerName.substring(lastDotIndex + 1)
            }
            val files: MutableList<FullRawFileObject> =
                this.fileService.getRawFilesByNameAndFolder(name, ext, parentId)
            val size = files.size
            if (size == 0) {
                return null
            }
            if (size == 1) {
                return files[0]
            }
            return files.stream().filter { e: FullRawFileObject -> e.type == FileType.FILE }.findFirst()
                .orElse(null)
        }
        val folderVo: FolderVo =
            folderMapper.selectOneByCondition(FOLDER_VO.NAME.eq(currentLayerName).and(FOLDER_VO.PARENT.eq(parentId)))
                ?: return null
        return resolveFile(path.subList(1, path.size), folderVo.id)
    }
}