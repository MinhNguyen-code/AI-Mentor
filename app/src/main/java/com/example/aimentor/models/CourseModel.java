package com.example.aimentor.models;

public class CourseModel {
    private int id;
    private String code;
    private String title;
    private int credits;
    private String description;
    private int totalQuestions;

    public CourseModel() {
    }

    public CourseModel(int id, String code, String title, int credits, String description, int totalQuestions) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.description = description;
        this.totalQuestions = totalQuestions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}
