package com.wu.service.entity;

import java.sql.Blob;

public class FileUploadRequest {
    private String binaryData;
    private String id;

    private String fileName;

    private String fileType;
    public FileUploadRequest() {
    }

    // 构造函数
    public FileUploadRequest(String id, String binaryData) {
        this.id = id;
        this.binaryData = binaryData;
    }

    // Getter方法
    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }
    public String getBinaryData() {
        return binaryData;
    }

    public String getFileType(){
        return fileType;
    }

    // Setter方法
    public void setId(String id) {
        this.id = id;
    }

    public void setBinaryData(String binaryData) {
        this.binaryData = binaryData;
    }
}
