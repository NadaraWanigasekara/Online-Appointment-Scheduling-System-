package com.example.mywebapp;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AdminManageAppointmentServlet")
public class AdminManageAppointmentServlet extends HttpServlet {
    private static final String FILE_NAME = "/WEB-INF/appointments.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        String appointmentId = request.getParameter("appointmentId");
        String patientName = request.getParameter("patientName");
        String doctorName = request.getParameter("doctorName");
        String appointmentDate = request.getParameter("appointmentDate");
        String appointmentTime = request.getParameter("appointmentTime");
        String status = request.getParameter("status");

        if (isNotEmpty(appointmentId) && isNotEmpty(patientName) && isNotEmpty(doctorName)
                && isNotEmpty(appointmentDate) && isNotEmpty(appointmentTime) && isNotEmpty(status)) {

            saveAppointmentToFile(new AdminManageAppointment(
                    appointmentId, patientName, doctorName, appointmentDate, appointmentTime, status));

            response.sendRedirect("admin_manageAppointment.html");
        } else {
            response.sendRedirect("admin_manageAppointment.html?error=missing_fields");
        }
    }

    private void saveAppointmentToFile(AdminManageAppointment newAppointment) {
        String filePath = getServletContext().getRealPath(FILE_NAME);
        CustomPriorityQueue<AdminManageAppointment> appointmentQueue = new CustomPriorityQueue<>();

        // Read existing appointments into the priority queue
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length == 6) {
                    appointmentQueue.add(new AdminManageAppointment(
                            data[0], data[1], data[2], data[3], data[4], data[5]));
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, which is fine for the first appointment
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the new appointment
        appointmentQueue.add(newAppointment);

        // Write all appointments back to the file in priority order
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            while (!appointmentQueue.isEmpty()) {
                AdminManageAppointment appointment = appointmentQueue.poll();
                out.println(appointment.toCSV());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        CustomPriorityQueue<AdminManageAppointment> appointmentQueue = new CustomPriorityQueue<>();
        String filePath = getServletContext().getRealPath(FILE_NAME);

        // Load appointments into the queue
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length == 6) {
                    appointmentQueue.add(new AdminManageAppointment(
                            data[0], data[1], data[2], data[3], data[4], data[5]));
                }
            }
        } catch (FileNotFoundException e) {
            out.println("<p>No appointments found.</p>");
        } catch (IOException e) {
            out.println("<p>Error loading appointment records.</p>");
            e.printStackTrace();
        }

        // Convert queue to array for bubble sort
        int size = appointmentQueue.size();
        AdminManageAppointment[] appointments = new AdminManageAppointment[size];
        int index = 0;
        while (!appointmentQueue.isEmpty()) {
            appointments[index++] = appointmentQueue.poll();
        }

        // Sort by time slot using Bubble Sort
        BubbleSort.sortByTime(appointments, size);

        // HTML Output
        out.println("<html><head><style>");
        out.println("table { width: 100%; border-collapse: collapse; background: rgba(30,30,50,0.95); border-radius: 10px; overflow: hidden; margin-top: 20px; }");
        out.println("th, td { padding: 14px 18px; text-align: center; border-bottom: 1px solid #444; color: #eee; }");
        out.println("th { background: #2c2c44; color: #61dafb; font-size: 16px; text-transform: uppercase; }");
        out.println("td { background: rgba(50,50,70,0.9); font-size: 15px; }");
        out.println("</style></head><body>");
        out.println("<table>");
        out.println("<tr><th>ID</th><th>Patient</th><th>Doctor</th><th>Date</th><th>Time</th><th>Status</th></tr>");

        if (size == 0) {
            out.println("<tr><td colspan='6'>No appointments available.</td></tr>");
        } else {
            for (int i = 0; i < size; i++) {
                AdminManageAppointment a = appointments[i];
                out.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                        escapeHtml(a.getAppointmentId()),
                        escapeHtml(a.getPatientName()),
                        escapeHtml(a.getDoctorName()),
                        escapeHtml(a.getAppointmentDate()),
                        escapeHtml(a.getAppointmentTime()),
                        escapeHtml(a.getStatus()));
            }
        }

        out.println("</table></body></html>");
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}