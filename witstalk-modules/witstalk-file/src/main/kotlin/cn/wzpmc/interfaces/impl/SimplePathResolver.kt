package cn.wzpmc.interfaces.impl

import cn.wzpmc.entities.files.RawFileObject
import cn.wzpmc.entities.vo.FolderVo
import cn.wzpmc.interfaces.FilePathService
import cn.wzpmc.mapper.FolderMapper

abstract class SimplePathResolver(val folderMapper: FolderMapper) : FilePathService {

    override fun getFilePath(file: RawFileObject): String {
        return resolvePath(file.parent) + RawFileObject.getRawFileName(file)
    }

    private fun resolvePath(id: Long): String {
        if (id == -1L) {
            return "/"
        }
        val folderVo: FolderVo = folderMapper.selectOneById(id)
        val parent: Long = folderVo.parent
        val name: String = folderVo.name
        return resolvePath(parent) + name + "/"
    }

    protected fun removeEmptyPath(path: Array<String>): MutableList<String> {
        return java.util.Arrays.stream(path).filter { e: String -> e.isEmpty() }.toList()
    }
}
