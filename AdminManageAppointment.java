package com.example.mywebapp;

public class AdminManageAppointment implements Comparable<AdminManageAppointment> {
    private String appointmentId;
    private String patientName;
    private String doctorName;
    private String appointmentDate;
    private String appointmentTime;
    private String status;

    // Constructor
    public AdminManageAppointment(String appointmentId, String patientName, String doctorName,
                                  String appointmentDate, String appointmentTime, String status) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters
    public String getAppointmentId() { return appointmentId; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }

    // Setters
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
    public void setStatus(String status) { this.status = status; }

    public String toCSV() {
        return appointmentId + "," + patientName + "," + doctorName + "," +
                appointmentDate + "," + appointmentTime + "," + status;
    }

    public int getPriorityValue(String status) {
        return switch (status.toLowerCase()) {
            case "emergency" -> 3;
            case "pending" -> 2;
            case "scheduled" -> 1;
            case "completed", "cancelled" -> 0;
            default -> -1;
        };
    }

    // Method to convert time (HH:MM) to minutes past midnight for sorting
    public int getTimeInMinutes() {
        String[] timeParts = appointmentTime.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return hours * 60 + minutes;
    }

    @Override
    public int compareTo(AdminManageAppointment other) {
        int thisPriority = getPriorityValue(this.status);
        int otherPriority = getPriorityValue(other.status);
        return Integer.compare(thisPriority, otherPriority); // For max-heap: higher priority is "greater"
    }

    @Override
    public String toString() {
        return "AdminManageAppointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}