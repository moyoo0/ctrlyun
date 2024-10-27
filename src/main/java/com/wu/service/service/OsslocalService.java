package com.wu.service.service;

import com.wu.service.entity.File;
import org.springframework.web.multipart.MultipartFile;

public interface OsslocalService {

    //上传头像到oss
    File upload(MultipartFile file, String catalogue, String userId);

    String delete(String id);


}

