package com.smis.data;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Files {
    private String UserName;
    private String FileName;
    private String FileUrl;
    private String FilePath;
    private long Date;


    public Files() {
    }


    public Files(String userName, String fileName, String fileUrl, String filePath, long date) {
        UserName = userName;
        FileName = fileName;
        FileUrl = fileUrl;
        FilePath = filePath;
        Date = date;
    }


    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }


}
