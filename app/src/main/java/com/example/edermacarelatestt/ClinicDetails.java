package com.example.edermacarelatestt;

public class ClinicDetails {

    private String clinicAddr1;
    private String clinicAddr2;
    private String clinicAddr3;
    private String startTime;
    private String endTime;

    // Required default constructor for Firestore serialization
    public ClinicDetails() {
    }

    public ClinicDetails(String clinicAddr1, String clinicAddr2, String clinicAddr3, String startTime, String endTime) {
        this.clinicAddr1 = clinicAddr1;
        this.clinicAddr2 = clinicAddr2;
        this.clinicAddr3 = clinicAddr3;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters
    public String getClinicAddr1() {
        return clinicAddr1;
    }

    public void setClinicAddr1(String clinicAddr1) {
        this.clinicAddr1 = clinicAddr1;
    }

    public String getClinicAddr2() {
        return clinicAddr2;
    }

    public void setClinicAddr2(String clinicAddr2) {
        this.clinicAddr2 = clinicAddr2;
    }

    public String getClinicAddr3() {
        return clinicAddr3;
    }

    public void setClinicAddr3(String clinicAddr3) {
        this.clinicAddr3 = clinicAddr3;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}

