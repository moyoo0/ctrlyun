package com.wu.service.controller;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wu.service.entity.File;
import com.wu.service.entity.User;
import com.wu.service.excepyionhandler.SpaceException;
import com.wu.service.service.FileService;
import com.wu.service.service.OsslocalService;
import com.wu.service.service.impl.OsslocalServiceImpl;
import com.wu.service.service.UserService;
import com.wu.service.utils.ConstanPropertiesUtils;
import com.wu.service.utils.InitVodCilent;
import com.wu.service.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@RestController
@RequestMapping("/eduoss/fileoss")
@CrossOrigin
//@CrossOrigin
public class OsslocalController {

    @Autowired
    private OsslocalService osslocalService;
    @Autowired
    private FileService fileService;

    @Autowired
    private UserService memberService;
/*
    //上传头像
    @ApiOperation(value = "根据用户id上传头像")
    @PostMapping("uploadFileAvatar")
    public R uploadOssFile(MultipartFile file) {
        //获取上传文件  MultipartFile
        //返回上传到oss的路径
        String url = ossService.uploadFileAvatar(file);
        return R.ok().data("url", url);
    }
*/
    //判断上传的文件类型
    @ApiOperation(value = "上传文件")
    @PostMapping("upload/{memid}")
    public R upload(MultipartFile file, @PathVariable String memid) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", memid);
        User one = memberService.getOne(wrapper);
        long neicun = one.getNeicun();
        long size = file.getSize();
        long result=neicun+ size;
        if (result<1073741824){
            User member=new User();
            member.setNeicun(result);
            member.setId(memid);
            boolean b = memberService.updateById(member);
            System.out.println(b);
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //获取文件类型
            String fileType = fileName.substring(fileName.lastIndexOf("."));
            String type = fileType.substring(1);
            OsslocalService osslocalService = new OsslocalServiceImpl();
            File file1 = osslocalService.upload(file, "/file" ,memid);
            if (file1.equals("")) {
                return R.error();
            }
            return R.ok().data("file", file1);
        }else {
            throw new SpaceException(20001,"内存溢出");
        }

    }

    @ApiOperation(value = "群组上传文件")
    @PostMapping("uploadgroup/{memid}")
    public R uploadgroup(MultipartFile file, @PathVariable String memid) {
        //获取文件名称
        String fileName = file.getOriginalFilename();
        //获取文件类型
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        String type = fileType.substring(1);
        OsslocalService osslocalService = new OsslocalServiceImpl();
        File file1 = osslocalService.upload(file, "/file" ,memid);
            if (file1.equals("")) {
                return R.error();
            }
            return R.ok().data("file", file1);

    }



}
