package com.vbuilds.model;

public class Photo {
    private int photoId;
    private int buildId;
    private String filePath;
    private String caption;

    public Photo() {}

    public Photo(int photoId, int buildId, String filePath, String caption) {
        this.photoId = photoId; this.buildId = buildId; this.filePath = filePath; this.caption = caption; }



    public int getPhotoId() { return photoId; }

    public int getBuildId() { return buildId; }

    public String getFilePath() { return filePath; }

    public String getCaption() { return caption; }


    public void setFilePath(String newFilePath) { this.filePath = newFilePath; }

    public void setCaption(String newCaption) { this.caption = newCaption; }

    public void setPhotoId(int PHOTOid) { this.photoId = PHOTOid; }

    public void setBuildId(int buildID) { this.buildId = buildID; }
}
