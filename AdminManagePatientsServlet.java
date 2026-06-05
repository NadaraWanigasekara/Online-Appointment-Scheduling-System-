package com.example.mywebapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/AdminManagePatientsServlet")
public class AdminManagePatientsServlet extends HttpServlet {
    private static final String FILE_NAME = "/WEB-INF/managePatients.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String contact = request.getParameter("contact");

        int age;
        try {
            age = Integer.parseInt(request.getParameter("age"));
        } catch (NumberFormatException e) {
            age = -1;
        }

        if (name != null && !name.isEmpty() && contact != null && !contact.isEmpty() && age > 0) {
            savePatientToFile(request, name, age, contact);
        }

        response.sendRedirect("AdminManagePatientsServlet");
    }

    private void savePatientToFile(HttpServletRequest request, String name, int age, String contact) {
        String filePath = getServletContext().getRealPath(FILE_NAME);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)))) {
            out.println(name + "," + age + "," + contact);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String filePath = getServletContext().getRealPath(FILE_NAME);
        List<AdminManagePatients> patients = new ArrayList<>();

        // Read data
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    patients.add(new AdminManagePatients(data[0], Integer.parseInt(data[1]), data[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Bubble Sort by age (ascending)
        for (int i = 0; i < patients.size() - 1; i++) {
            for (int j = 0; j < patients.size() - i - 1; j++) {
                if (patients.get(j).getAge() > patients.get(j + 1).getAge()) {
                    AdminManagePatients temp = patients.get(j);
                    patients.set(j, patients.get(j + 1));
                    patients.set(j + 1, temp);
                }
            }
        }

        // Render HTML with CSS link
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html><head><title>Manage Patients</title>");
        out.println("<link rel='stylesheet' href='admin_managePatients.css'>");
        out.println("</head><body>");

        out.println("<nav><h2>Admin Panel - Manage Patients</h2><ul>" +
                "<li><a href='admin_dashboard.html'>Dashboard</a></li>" +
                "<li><a href='admin_manageAppointment.html'>Manage Appointments</a></li>" +
                "<li><a href='admin_manageDoctors.html'>Manage Doctors</a></li>" +
                "<li><a href='admin_viewReports.html'>View Reports</a></li>" +
                "</ul></nav>");

        out.println("<div class='container'>");
        out.println("<h1>Patient Management</h1>");
        out.println("<form method='POST' action='AdminManagePatientsServlet'>" +
                "<label for='name'>Patient Name:</label>" +
                "<input type='text' name='name' required>" +

                "<label for='age'>Age:</label>" +
                "<input type='number' name='age' required>" +

                "<label for='contact'>Contact:</label>" +
                "<input type='text' name='contact' required>" +

                "<button type='submit'>Save Patient</button>" +
                "</form>");

        // Table
        out.println("<h2>Patient Records (Sorted by Age)</h2><table><thead><tr><th>Name</th><th>Age</th><th>Contact</th></tr></thead><tbody>");
        for (AdminManagePatients p : patients) {
            out.println("<tr><td>" + p.getName() + "</td><td>" + p.getAge() + "</td><td>" + p.getContact() + "</td></tr>");
        }
        out.println("</tbody></table>");
        out.println("</div></body></html>");
    }
}
