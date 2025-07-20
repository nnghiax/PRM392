package com.example.prm392app.model;

public class InterviewSlot {
    private String id;
    private String applicationId;
    private String recruiterId;
    private String studentId;
    private String proposedDateTime;
    private String status; // PENDING, ACCEPTED, DECLINED
    private String companyName;
    private String jobTitle;

    public InterviewSlot() {
        // Required empty constructor for Firebase
    }

    public InterviewSlot(String applicationId, String recruiterId, String studentId, String proposedDateTime, String companyName, String jobTitle) {
        this.applicationId = applicationId;
        this.recruiterId = recruiterId;
        this.studentId = studentId;
        this.proposedDateTime = proposedDateTime;
        this.status = "PENDING";
        this.companyName = companyName;
        this.jobTitle = jobTitle;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(String recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProposedDateTime() {
        return proposedDateTime;
    }

    public void setProposedDateTime(String proposedDateTime) {
        this.proposedDateTime = proposedDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
