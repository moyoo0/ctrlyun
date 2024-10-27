package com.wu.service.service;

import com.wu.service.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;


public interface FileService extends IService<File> {


    List<File> getAllFileInfo(String memId);

    List<File> getFileInfo(String id);

    List<File> getCurFiles(String userDir,String id);


    List<File> getDeleteFiles(String dir, String id);

    File getFiles(String id);

    List<File> getFindFile(String memid,String name);

    List<File> getList(String memid, String url,int result,String name);

    public byte[] readFileToByteArray(String filePath) throws IOException;
}
