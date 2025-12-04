package cn.wzpmc.mapper

import cn.wzpmc.entities.file.vo.FolderVo
import com.mybatisflex.core.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface FolderMapper : BaseMapper<FolderVo>