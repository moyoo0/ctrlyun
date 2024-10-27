package com.wu.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.service.entity.GroupMem;
import com.wu.service.entity.GroupRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupRequestMapper extends BaseMapper<GroupRequest> {
    @Select("SELECT MAX(request_id) FROM group_request")
    Integer selectMaxValueOfColumn();

    @Select("SELECT * FROM group_request WHERE group_id = #{id}")
    List<GroupRequest> selectGroupByID(String id);


}
