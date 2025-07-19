package com.example.prm392app.model;

public class User {
    private String uid;
    private String email;
    private String name;
    private String role;
    private String university;
    private String company;
    private long created_at;

    public User() {
    }


    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getUniversity() {
        return university;
    }

    public String getCompany() {
        return company;
    }

    public long getCreated_at() {
        return created_at;
    }

    // Setter
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public String getDisplayName() {
        if (name != null && !name.isEmpty()) {
            return name;
        } else if (company != null && !company.isEmpty()) {
            return company;
        } else {
            return email;
        }
    }
}
