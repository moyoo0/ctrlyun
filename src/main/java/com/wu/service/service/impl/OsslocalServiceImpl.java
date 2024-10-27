package com.wu.service.service.impl;

import com.wu.service.mapper.FileMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.io.File;
import com.wu.service.service.FileService;
import com.wu.service.service.OsslocalService;
import com.wu.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class OsslocalServiceImpl implements OsslocalService {
    //

    @Autowired
    private FileService fileService;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserService memberService;


    //上传头像到oss
    @Override
    public com.wu.service.entity.File upload(MultipartFile file, String catalogue, String userId) {

        try {
            InputStream fileInputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            System.out.println(catalogue);


            // 构建用户目录的路径，例如：C:\Users\<用户名>\<用户ID>
            String userFolderPath = "D:\\" + userId;

            // 检查用户目录是否存在，如果不存在则创建它
            File userFolder = new File(userFolderPath);
            if (!userFolder.exists()) {
                userFolder.mkdirs(); // 创建用户目录及其父目录
            }


            String localSavePath = userFolderPath + "\\" + originalFilename;

            File localDirectory = new File(localSavePath.substring(0, localSavePath.lastIndexOf(File.separator)));
            if (!localDirectory.exists()) {
                localDirectory.mkdirs(); // 创建目录，包括父目录
            }
            try (OutputStream outputStream = new FileOutputStream(localSavePath)) {
                int readBytes;
                byte[] buffer = new byte[4096];
                while ((readBytes = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readBytes);
                }
            }
            com.wu.service.entity.File file1 = new com.wu.service.entity.File();
            file1.setName(originalFilename.substring(0, originalFilename.indexOf(".")));
            file1.setType(originalFilename.substring(originalFilename.lastIndexOf(".") + 1));
            file1.setUrl(localSavePath); // 设置本地文件路径
            file1.setFDir(catalogue);
            // 获取文件大小（字节数）
            file1.setSize(new File(localSavePath).length());
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 文件删除
     * @param: objectName
     * @return: java.lang.String
     * @create: 2020/10/31 16:50
     * @author: csp1999
     */
    public String delete(String id) {
     try {
         QueryWrapper<com.wu.service.entity.File> wrapper = new QueryWrapper<>();
         System.out.println(id);
         wrapper.eq("id", id);
         // 使用FileMapper的selectOne方法查询符合条件的记录
         com.wu.service.entity.File fileServiceOne = fileService.getOne(wrapper);
         String name = fileServiceOne.getName();
         String localFilePath = fileServiceOne.getUrl();
         File localFile = new File(localFilePath);
         if (localFile.exists()) {
             if (localFile.delete()) {
                 System.out.println("本地文件删除成功");
             } else {
                 System.out.println("本地文件删除失败");
             }
         }

         boolean remove = fileService.remove(wrapper);
         if (remove) {
             System.out.println("删除成功");
             return "success";
         } else {
             System.out.println("删除失败");
             return "error";
         }
     } catch (Exception e){
         e.printStackTrace();
         return "error";
     }
    }





}
