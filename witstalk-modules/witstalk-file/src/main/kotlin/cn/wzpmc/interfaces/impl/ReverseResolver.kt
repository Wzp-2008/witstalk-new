package cn.wzpmc.interfaces.impl

import cn.wzpmc.entities.files.FullRawFileObject
import cn.wzpmc.entities.vo.FolderVo
import cn.wzpmc.mapper.FolderMapper
import cn.wzpmc.service.FileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReverseResolver(folderMapper: FolderMapper) : SimplePathResolver(folderMapper) {
    private lateinit var fileService: FileService

    @Autowired
    @Lazy
    fun setFileService(fileService: FileService) {
        this.fileService = fileService
    }
    override fun resolveFile(path: Array<String>): FullRawFileObject? {
        val pathList: MutableList<String> = removeEmptyPath(path)
        val targetFileName = pathList[pathList.size - 1]
        val lastDotIndex = targetFileName.lastIndexOf('.')
        var name = targetFileName
        var ext = ""
        if (lastDotIndex != -1) {
            name = targetFileName.take(lastDotIndex)
            ext = targetFileName.substring(lastDotIndex + 1)
        }
        val rawFileObjects: MutableList<FullRawFileObject> =
            fileService.getRawFilesByName(name, ext)
        if (rawFileObjects.isEmpty()) return null
        if (rawFileObjects.size == 1) return rawFileObjects[0]
        val possibleParents: List<Long> =
            rawFileObjects.map { it.parent }
        val inRoot: Optional<FullRawFileObject> =
            rawFileObjects.stream().filter { e: FullRawFileObject -> e.parent == -1L }.findFirst()
        if (inRoot.isPresent) {
            if (pathList.size <= 1) {
                return inRoot.get()
            }
        }
        val folderVos: MutableList<FolderVo> = folderMapper.selectListByIds(possibleParents)
        val parent: FolderVo = reverseFindFileParent(folderVos, pathList.subList(0, pathList.size - 1)) ?: return null
        val first: Optional<FullRawFileObject> =
            rawFileObjects.stream().filter { e: FullRawFileObject -> e.parent == parent.id }.findFirst()
        return first.orElse(null)
    }

    private fun reverseFindFileParent(
        possibleParent: MutableList<FolderVo>,
        path: MutableList<String>
    ): FolderVo? {
        if (path.isEmpty()) return null
        if (possibleParent.size == 1) return possibleParent[0]
        val currentLayerName = path[path.size - 1]
        val folderVoStream: MutableList<FolderVo> =
            possibleParent.stream().filter { e: FolderVo -> e.name == currentLayerName }.toList()
        val inRoot: Optional<FolderVo> =
            folderVoStream.stream().filter { e: FolderVo -> e.parent == -1L }.findFirst()
        if (inRoot.isPresent) {
            if (path.size <= 1) {
                return inRoot.get()
            }
        }
        val list: List<Long> = folderVoStream.map { e: FolderVo -> e.id }
        if (list.isEmpty()) return null
        val parents: MutableList<FolderVo> = folderMapper.selectListByIds(list)
        val parent: FolderVo = reverseFindFileParent(parents, path.subList(0, path.size - 1)) ?: return null
        val first: Optional<FolderVo> =
            Optional.ofNullable(folderVoStream.firstOrNull { e: FolderVo -> e.parent == parent.id })
        return first.orElse(null)
    }
}