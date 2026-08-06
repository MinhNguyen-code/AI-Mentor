package com.example.aimentor.models;

public class UserCourseModel {
    private int id;
    private int userId;
    private String courseCode;
    private String courseTitle;
    private String explanationStyle;
    private int progressPercent;
    private String status;
    private int questionsAsked;
    private String createdAt;

    public UserCourseModel() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getExplanationStyle() { return explanationStyle; }
    public void setExplanationStyle(String explanationStyle) { this.explanationStyle = explanationStyle; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQuestionsAsked() { return questionsAsked; }
    public void setQuestionsAsked(int questionsAsked) { this.questionsAsked = questionsAsked; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
