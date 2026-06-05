package com.example.mywebapp;

public class AdminManageDoctors {
    private String doctorId;
    private String name;
    private String specialization;
    private int experience; // Ensure experience is stored as an integer for sorting

    // Constructor
    public AdminManageDoctors(String doctorId, String name, String specialization, int experience) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
    }

    // Getters
    public String getDoctorId() {
        return doctorId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getExperience() {
        return experience;
    }

    // Setters (optional, if needed)
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    // Convert doctor details to CSV format (for storage)
    public String toCSV() {
        return doctorId + "," + name + "," + specialization + "," + experience;
    }

    // String Representation for Debugging
    @Override
    public String toString() {
        return "AdminManageDoctors{" +
                "doctorId='" + doctorId + '\'' +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                ", experience=" + experience +
                '}';
    }
}
