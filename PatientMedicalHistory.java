package com.example.mywebapp;

public class PatientMedicalHistory {
    private String name;
    private String date;
    private String diagnosis;
    private String prescription;
    private String doctor;

    public PatientMedicalHistory(String name, String date, String diagnosis, String prescription, String doctor) {
        this.name = name;
        this.date = date;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.doctor = doctor;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public String getDiagnosis() { return diagnosis; }
    public String getPrescription() { return prescription; }
    public String getDoctor() { return doctor; }

    public String toCSV() {
        return name + "," + date + "," + diagnosis + "," + prescription + "," + doctor;
    }
}