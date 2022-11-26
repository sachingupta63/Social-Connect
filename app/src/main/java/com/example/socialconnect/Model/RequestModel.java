package com.example.socialconnect.Model;

//When a user click follow then ac hild created with requested account
public class RequestModel {
    String name;
    String url;
    String userid;

    public RequestModel(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


}
