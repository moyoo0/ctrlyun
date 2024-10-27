package com.wu.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.service.entity.UserDir;


public interface UserDirService extends IService<UserDir> {

    UserDir getUserDir(String id);

    int setUserDir(UserDir userDir);

    boolean deleteStruct(String memid, String url);
}
