package com.example.prm392app.model;

public class Application {
    private String applicationId;
    private String studentId;
    private String internshipId;
    private String resumeUrl;
    private String status;
    private long appliedAt;

    public Application() {}

    public Application(String applicationId, String studentId, String internshipId, String resumeUrl, String status, long appliedAt) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.resumeUrl = resumeUrl;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getInternshipId() { return internshipId; }
    public void setInternshipId(String internshipId) { this.internshipId = internshipId; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getAppliedAt() { return appliedAt; }
    public void setAppliedAt(long appliedAt) { this.appliedAt = appliedAt; }
}
