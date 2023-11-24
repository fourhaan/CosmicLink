package com.example.volunteerapp.AdminPanel.Models;

import java.util.Map;

public class ReportedPost {
    private String reportedById,postId;
    private long timestamp;
    private boolean harassment;
    private boolean inappropriateContent;
    private boolean other;
    private boolean spam;

    public ReportedPost() {
        // Default constructor required for Firebase
    }

    public ReportedPost(String postId,String reportedById, long timestamp, boolean harassment, boolean inappropriateContent, boolean other, boolean spam) {
        this.reportedById = reportedById;
        this.timestamp = timestamp;
        this.harassment = harassment;
        this.inappropriateContent = inappropriateContent;
        this.other = other;
        this.spam = spam;
        this.postId = postId;
    }


    public String getReportedById() {
        return reportedById;
    }

    public void setReportedById(String reportedById) {
        this.reportedById = reportedById;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHarassment() {
        return harassment;
    }

    public void setHarassment(boolean harassment) {
        this.harassment = harassment;
    }

    public boolean isInappropriateContent() {
        return inappropriateContent;
    }

    public void setInappropriateContent(boolean inappropriateContent) {
        this.inappropriateContent = inappropriateContent;
    }

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    public boolean isSpam() {
        return spam;
    }

    public void setSpam(boolean spam) {
        this.spam = spam;
    }
}

