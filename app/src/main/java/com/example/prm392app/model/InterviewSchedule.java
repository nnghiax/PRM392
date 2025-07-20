package com.example.prm392app.model;

public class InterviewSchedule {
    private String scheduleId;
    private String applicationId;
    private String proposedDateTime;
    private String status; // PENDING, ACCEPTED, DECLINED
    private String note;

    public InterviewSchedule() {}

    public InterviewSchedule(String scheduleId, String applicationId, String proposedDateTime, String status, String note) {
        this.scheduleId = scheduleId;
        this.applicationId = applicationId;
        this.proposedDateTime = proposedDateTime;
        this.status = status;
        this.note = note;
    }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getProposedDateTime() { return proposedDateTime; }
    public void setProposedDateTime(String proposedDateTime) { this.proposedDateTime = proposedDateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
