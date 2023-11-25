package com.example.volunteerapp.AdminPanel.Models;

public class ReportedUser {
    private String reportedUserId;
    private String reportedByUserId;
    private long timestamp;

    public ReportedUser() {
    }

    public ReportedUser(String reportedUserId, String reportedByUserId, long timestamp) {
        this.reportedUserId = reportedUserId;
        this.reportedByUserId = reportedByUserId;
        this.timestamp = timestamp;
    }

    public String getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(String reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportedByUserId() {
        return reportedByUserId;
    }

    public void setReportedByUserId(String reportedByUserId) {
        this.reportedByUserId = reportedByUserId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

