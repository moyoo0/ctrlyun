package com.wu.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.service.entity.User;


public interface UcenterMemberMapper extends BaseMapper<User> {

    Integer registerCount(String day);
}
