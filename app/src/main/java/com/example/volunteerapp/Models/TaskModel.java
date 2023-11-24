package com.example.volunteerapp.Models;



public class TaskModel {
    String title;
    String content;
    long timestamp;
    int taskhours, tasknum;

    public TaskModel() {

    }

//    public TaskModel(String title , String content, long timestamp,int taskhours) {
//        this.title = title;
//        this.content = content;
//        this.timestamp = timestamp;
//        this.taskhours = taskhours;
//    }

    public TaskModel(String title , String content, long timestamp, int taskhours, int tasknum) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.taskhours = taskhours;
        this.tasknum = tasknum;
    }

    public int getTasknum() {
        return tasknum;
    }

    public void setTasknum(int tasknum) {
        this.tasknum = tasknum;
    }

    public String getTitle() {
        return title;
    }

    public int getTaskhours() {
        return taskhours;
    }

    public void setTaskhours(int taskhours) {
        this.taskhours = taskhours;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}