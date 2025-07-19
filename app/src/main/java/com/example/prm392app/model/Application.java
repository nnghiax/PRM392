
package com.example.prm392app.model;

public class Application {
    private String applicationId;
    private String studentId;
    private String internshipId;
    private String fullName;
    private String dateOfBirth; // Ngày tháng năm sinh (tùy chọn)
    private String phoneNumber;
    private String email;
    private String address; // Địa chỉ nơi ở (rút gọn)
    private String companyName; // Tên công ty/tổ chức tuyển dụng
    private String department; // Phòng ban tuyển dụng (tùy chọn)
    private String recipientTitle; // Chức danh người nhận (tùy chọn)
    private String applicationTitle; // Tiêu đề đơn (ví dụ: ĐƠN XIN VIỆC)
    private String introduction; // Lời mở đầu (lý do viết đơn, nguồn thông tin, vị trí ứng tuyển)
    private String personalSummary; // Trình bày ngắn gọn về bản thân (học vấn, kỹ năng, kinh nghiệm)
    private String motivation; // Lý do ứng tuyển và mong muốn đóng góp
    private String commitment; // Lời cam kết và mong muốn phỏng vấn
    private String applicationDate; // Ngày viết đơn
    private String status;
    private long appliedAt;

    public Application() {}

    public Application(String applicationId, String studentId, String internshipId, String fullName, String dateOfBirth,
                       String phoneNumber, String email, String address, String companyName, String department,
                       String recipientTitle, String applicationTitle, String introduction, String personalSummary,
                       String motivation, String commitment, String applicationDate, String status, long appliedAt) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.companyName = companyName;
        this.department = department;
        this.recipientTitle = recipientTitle;
        this.applicationTitle = applicationTitle;
        this.introduction = introduction;
        this.personalSummary = personalSummary;
        this.motivation = motivation;
        this.commitment = commitment;
        this.applicationDate = applicationDate;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getInternshipId() { return internshipId; }
    public void setInternshipId(String internshipId) { this.internshipId = internshipId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRecipientTitle() { return recipientTitle; }
    public void setRecipientTitle(String recipientTitle) { this.recipientTitle = recipientTitle; }
    public String getApplicationTitle() { return applicationTitle; }
    public void setApplicationTitle(String applicationTitle) { this.applicationTitle = applicationTitle; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getPersonalSummary() { return personalSummary; }
    public void setPersonalSummary(String personalSummary) { this.personalSummary = personalSummary; }
    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }
    public String getCommitment() { return commitment; }
    public void setCommitment(String commitment) { this.commitment = commitment; }
    public String getApplicationDate() { return applicationDate; }
    public void setApplicationDate(String applicationDate) { this.applicationDate = applicationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getAppliedAt() { return appliedAt; }
    public void setAppliedAt(long appliedAt) { this.appliedAt = appliedAt; }
}
