package cn.wzpmc.interfaces

import cn.wzpmc.entities.FullRawFileObject
import cn.wzpmc.entities.RawFileObject


interface FilePathService {
    fun getFilePath(file: RawFileObject): String

    fun resolveFile(path: Array<String>): FullRawFileObject?
}
