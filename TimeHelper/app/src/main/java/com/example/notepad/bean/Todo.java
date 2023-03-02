package com.example.notepad.bean;

import java.io.Serializable;

public class Todo implements Serializable {
    private String id;
    private String title;
    private String content;
    private String createdTime;
    private String startTime;
    private String endTime;
    private int done;
    private int star;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String toJson() {
        return "{\n" +
                "\"id\": \""+id+"\",\n" +
                "\"title\": \""+title+"\",\n" +
                "\"content\": \""+content+"\",\n" +
                "\"createdTime\": \""+createdTime+"\",\n" +
                "\"startTime\": \""+startTime+"\",\n" +
                "\"endTime\": \""+endTime+"\",\n" +
                "\"done\": \""+done+"\",\n" +
                "\"star\": \""+star+"\"\n" +
                "}";
    }
}
