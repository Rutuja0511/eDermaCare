package com.example.edermacarelatestt;

public class Dermatologist {
    private String Name;

    private String City;

    // Empty constructor required for Firestore deserialization
    public Dermatologist() {
    }

    public Dermatologist(String Name, String City) {
        this.Name = Name;
        this.City = City;
    }

    public String getName() {
        return Name;
    }

    public String getCity() {
        return City;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setCity(String City) {
        this.City = City;
    }
}
