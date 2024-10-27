package com.wu.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.service.entity.User;
import com.wu.service.entity.vo.FindPasswordVo;
import com.wu.service.entity.vo.RegisterVo;


public interface UserService extends IService<User> {

    //登录的方法
    String login(User member);

    User login1(User member);
    //注册的方法
    void register(RegisterVo registerVo);

    void registerByEmail(RegisterVo registerVo);

    int findPassword(FindPasswordVo findPasswordVo);


//    boolean updateMember(User ucenterMember);
}
