package cn.wzpmc.interfaces

import cn.wzpmc.entities.files.FullRawFileObject
import cn.wzpmc.entities.files.RawFileObject

interface FilePathService {
    fun getFilePath(file: RawFileObject): String

    fun resolveFile(path: Array<String>): FullRawFileObject?
}
