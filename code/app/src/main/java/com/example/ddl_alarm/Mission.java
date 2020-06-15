package com.example.ddl_alarm;

import java.util.Date;

public class Mission {
    private String base64;
    private String title;
    private String content;
    private Date ddl;
    private int id;

    public Mission(String title, String base64, String content, Date ddl, int id){
        this.title = title;
        this.base64 = base64;
        this.content = content;
        this.ddl = ddl;
        this.id = id;
    }
    String getBase64(){
        return this.base64;
    }
    String getTitle(){
        return this.title;
    }
    String getContent(){
        return this.content;
    }
    Date getDdl(){
        return this.ddl;
    }
    int getId(){
        return this.id;
    }
}