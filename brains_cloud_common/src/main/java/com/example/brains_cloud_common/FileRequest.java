package com.example.brains_cloud_common;

public class FileRequest  extends AbstractMessage{
    // передача запроса
    private String fileName;


    public String getFileName() {
        return fileName;
    }

    public FileRequest(String fileName) {
        this.fileName = fileName;
    }
}
