package com.example.mywebapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet("/AdminViewReportsServlet")
public class AdminViewReportsServlet extends HttpServlet {

    private static final String FILE_NAME = "/WEB-INF/reports.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String patientName = request.getParameter("patientName");
        String clinicDateStr = request.getParameter("clinicDate");
        String clinicDetails = request.getParameter("clinicDetails");
        String assignedStaff = request.getParameter("assignedStaff");
        String status = request.getParameter("status");

        if (isNotEmpty(patientName) && isNotEmpty(clinicDateStr) && isNotEmpty(clinicDetails)
                && isNotEmpty(assignedStaff) && isNotEmpty(status)) {
            saveReportToFile(request, patientName, clinicDateStr, clinicDetails, assignedStaff, status);
        }

        // Redirect back to GET view after POST to prevent form resubmission on refresh
        response.sendRedirect("AdminViewReportsServlet");
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void saveReportToFile(HttpServletRequest request, String patientName, String clinicDateStr,
                                  String clinicDetails, String assignedStaff, String status) throws IOException {

        String filePath = getServletContext().getRealPath(FILE_NAME);
        File file = new File(filePath);

        // Ensure file exists
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            // Escape commas and new lines, and save date in ISO format
            out.println(String.join(",",
                    escapeCsv(patientName),
                    escapeCsv(clinicDateStr),
                    escapeCsv(clinicDetails),
                    escapeCsv(assignedStaff),
                    escapeCsv(status)));
        }
    }

    // Replace commas with spaces, and remove new lines to keep CSV safe
    private String escapeCsv(String text) {
        if (text == null) return "";
        return text.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String filePath = getServletContext().getRealPath(FILE_NAME);
        List<PatientCleanicReport> reports = new ArrayList<>();

        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",", -1);
                    if (data.length == 5) {
                        try {
                            LocalDate clinicDate = LocalDate.parse(data[1], DATE_FORMATTER);
                            reports.add(new PatientCleanicReport(
                                    data[0],
                                    clinicDate, // Store LocalDate object
                                    data[2],
                                    data[3],
                                    data[4]
                            ));
                        } catch (DateTimeParseException e) {
                            // Handle cases where the date format might be incorrect in the file
                            System.err.println("Error parsing date: " + data[1] + " - " + e.getMessage());
                            // Optionally, you could skip this report or handle it differently
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Sort the reports by clinic date using bubble sort
        bubbleSortByDate(reports);

        // Start HTML response
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Admin - View Reports</title>");
        out.println("<link rel='stylesheet' href='admin_viewReport.css'>");
        out.println("</head><body>");

        out.println("<nav><h2>Admin Panel - View Reports</h2><ul>" +
                "<li><a href='admin_dashboard.html'>Dashboard</a></li>" +
                "<li><a href='admin_manageAppointment.html'>Manage Appointments</a></li>" +
                "<li><a href='admin_manageDoctors.html'>Manage Doctors</a></li>" +
                "<li><a href='admin_managePatients.html'>Manage Patients</a></li>" +
                "<li><a href='admin_settings.html'>Settings</a></li>" +
                "<li><a href='../index.html'>Logout</a></li>" +
                "</ul></nav>");

        out.println("<div class='container'>");
        out.println("<h1>Submit Patient Clinic Report</h1>");
        out.println("<form action='AdminViewReportsServlet' method='POST'>" +
                "<label for='patientName'>Patient Name:</label>" +
                "<input type='text' id='patientName' name='patientName' required>" +
                "<label for='clinicDate'>Clinic Date:</label>" +
                "<input type='date' id='clinicDate' name='clinicDate' required>" +
                "<label for='clinicDetails'>Clinic Details:</label>" +
                "<textarea id='clinicDetails' name='clinicDetails' rows='4' required></textarea>" +
                "<label for='assignedStaff'>Assigned Staff:</label>" +
                "<input type='text' id='assignedStaff' name='assignedStaff' required>" +
                "<label for='status'>Status:</label>" +
                "<select id='status' name='status' required>" +
                "<option value='Completed'>Completed</option>" +
                "<option value='Pending'>Pending</option>" +
                "<option value='Cancelled'>Cancelled</option>" +
                "</select>" +
                "<button type='submit'>Save Report</button>" +
                "</form>");

        out.println("<h2>Clinic Reports</h2>");
        out.println("<table>");
        out.println("<tr><th>Patient Name</th><th>Date</th><th>Details</th><th>Staff</th><th>Status</th></tr>");

        if (reports.isEmpty()) {
            out.println("<tr><td colspan='5'>No reports available.</td></tr>");
        } else {
            for (PatientCleanicReport report : reports) {
                out.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                        escapeHtml(report.getPatientName()),
                        escapeHtml(report.getClinicDate().format(DATE_FORMATTER)), // Format LocalDate for display
                        escapeHtml(report.getClinicDetails()),
                        escapeHtml(report.getAssignedStaff()),
                        escapeHtml(report.getStatus()));
            }
        }
        out.println("</table>");
        out.println("</div>");

        out.println("</body></html>");
        out.close();
    }

    // Bubble sort implementation to sort reports by date
    private void bubbleSortByDate(List<PatientCleanicReport> reports) {
        int n = reports.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (reports.get(j).getClinicDate().isAfter(reports.get(j + 1).getClinicDate())) {
                    // Swap reports[j] and reports[j+1]
                    PatientCleanicReport temp = reports.get(j);
                    reports.set(j, reports.get(j + 1));
                    reports.set(j + 1, temp);
                    swapped = true;
                }
            }
            // If no two elements were swapped in inner loop, the array is sorted
            if (!swapped) {
                break;
            }
        }
    }

    // Simple HTML escape method to avoid injection issues
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}