package cn.wzpmc.mapper

import cn.wzpmc.entities.vo.FileVo
import com.mybatisflex.core.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface FileMapper : BaseMapper<FileVo>