package com.smis.data;

public class FileDetails {
    private String fileName;
    private String downloadURL;
    private String name;
    private String thumbnailURL;

    public FileDetails(String fileName, String downloadURL, String name, String thumbnailURL) {
        this.fileName = fileName;
        this.downloadURL = downloadURL;
        this.name = name;
        this.thumbnailURL = thumbnailURL;
    }

    public FileDetails() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public String toString() {
        return "FileDetails{" +
                "fileName='" + fileName + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                ", name='" + name + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                '}';
    }
}