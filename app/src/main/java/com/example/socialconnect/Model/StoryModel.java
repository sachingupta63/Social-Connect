package com.example.socialconnect.Model;

public class StoryModel {

    String postUri,name,timeUpload,type,caption,url,uid;
    long timeEnd;

    public StoryModel(){}
    public String getPostUri() {
        return postUri;
    }

    public void setPostUri(String postUri) {
        this.postUri = postUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeUpload() {
        return timeUpload;
    }

    public void setTimeUpload(String timeUpload) {
        this.timeUpload = timeUpload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }
}

