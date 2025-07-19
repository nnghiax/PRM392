
package com.example.prm392app.model;

public class Internship {
    private String internshipId;
    private String jobTitle;
    private String companyId;
    private String companyName;
    private String locationAddress;
    private Double latitude;
    private Double longitude;
    private String duration;
    private String field;
    private String description;
    private String requirements;
    private Double stipend;
    private Long deadline;
    private Long postedAt;

    public Internship() {}

    public Internship(String internshipId, String jobTitle, String companyId, String companyName,
                      String locationAddress, Double latitude, Double longitude, String duration,
                      String field, String description, String requirements, Double stipend,
                      Long deadline, Long postedAt) {
        this.internshipId = internshipId;
        this.jobTitle = jobTitle;
        this.companyId = companyId;
        this.companyName = companyName;
        this.locationAddress = locationAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.duration = duration;
        this.field = field;
        this.description = description;
        this.requirements = requirements;
        this.stipend = stipend;
        this.deadline = deadline;
        this.postedAt = postedAt;
    }

    public String getInternshipId() { return internshipId; }
    public void setInternshipId(String internshipId) { this.internshipId = internshipId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String locationAddress) { this.locationAddress = locationAddress; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public Double getStipend() { return stipend; }
    public void setStipend(Double stipend) { this.stipend = stipend; }
    public Long getDeadline() { return deadline; }
    public void setDeadline(Long deadline) { this.deadline = deadline; }
    public Long getPostedAt() { return postedAt; }
    public void setPostedAt(Long postedAt) { this.postedAt = postedAt; }
}
