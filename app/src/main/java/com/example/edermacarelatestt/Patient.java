package com.example.edermacarelatestt;

public class Patient {
    private String name;
    private String email;
    private String dob;
    private String mobile;
    private String hashedPassword;
    private String gender;

    public Patient(String name, String email, String dob, String mobile, String hashedPassword, String gender) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.mobile = mobile;
        this.hashedPassword = hashedPassword;
        this.gender = gender;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
