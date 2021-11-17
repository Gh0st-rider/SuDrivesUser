package com.sudrives.sudrives.firebase;

public class InsertMessagesModel {
    private String msg;
    private String id;
    private String isUser;
    private String chatTime;

    public InsertMessagesModel(String msg, String id, String isUser,String chatTime) {
        this.msg = msg;
        this.id = id;
        this.isUser = isUser;
        this.chatTime = chatTime;
    }

    public InsertMessagesModel() {

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsUser() {
        return isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }
}

