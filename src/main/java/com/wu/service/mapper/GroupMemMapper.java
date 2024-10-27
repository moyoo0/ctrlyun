package com.wu.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.service.entity.GroupMem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupMemMapper extends BaseMapper<GroupMem> {
    @Select("SELECT MAX(record) FROM group_mem")
    Integer selectMaxValueOfColumn();

    @Select("SELECT * FROM group_mem WHERE mem_id = #{id}")
    List<GroupMem> selectGroupByID(String id);
}
