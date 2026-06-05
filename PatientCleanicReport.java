package com.example.mywebapp;

import java.time.LocalDate;

public class PatientCleanicReport {
    private String patientName;
    private LocalDate clinicDate; // Changed to LocalDate
    private String clinicDetails;
    private String assignedStaff;
    private String status;

    public PatientCleanicReport(String patientName, LocalDate clinicDate, String clinicDetails, String assignedStaff, String status) {
        this.patientName = patientName;
        this.clinicDate = clinicDate;
        this.clinicDetails = clinicDetails;
        this.assignedStaff = assignedStaff;
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDate getClinicDate() {
        return clinicDate;
    }

    public void setClinicDate(LocalDate clinicDate) {
        this.clinicDate = clinicDate;
    }

    public String getClinicDetails() {
        return clinicDetails;
    }

    public void setClinicDetails(String clinicDetails) {
        this.clinicDetails = clinicDetails;
    }

    public String getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(String assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PatientCleanicReport{" +
                "patientName='" + patientName + '\'' +
                ", clinicDate=" + clinicDate +
                ", clinicDetails='" + clinicDetails + '\'' +
                ", assignedStaff='" + assignedStaff + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}