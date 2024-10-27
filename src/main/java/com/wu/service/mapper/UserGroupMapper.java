package com.wu.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.service.entity.UserGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserGroupMapper extends BaseMapper<UserGroup> {

    @Select("SELECT group_id FROM user_group WHERE master_id = #{MasterID}")
    String[] selectGroupID(String MasterID);


}
