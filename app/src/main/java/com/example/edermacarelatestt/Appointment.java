package com.example.edermacarelatestt;
public class Appointment {
    private String name;
    private String city;
    private String district;
    private String state;
    private String email;
    private String mobile;
    private String experience;
    private String date;
    private String time;

    public Appointment() {
        // Empty constructor required for Firestore
    }

    public Appointment(String name, String city, String district, String state, String email, String mobile, String experience, String date, String time) {
        this.name = name;
        this.city = city;
        this.district = district;
        this.state = state;
        this.email = email;
        this.mobile = mobile;
        this.experience = experience;
        this.date = date;
        this.time = time;
    }

    // Getters and setters
}
