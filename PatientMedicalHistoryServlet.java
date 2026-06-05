package com.example.mywebapp;

import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/PatientMedicalHistoryServlet")
public class PatientMedicalHistoryServlet extends HttpServlet {
    private static final String FILE_NAME = "/WEB-INF/medicalhistory.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        String date = request.getParameter("date");
        String diagnosis = request.getParameter("diagnosis");
        String prescription = request.getParameter("prescription");
        String doctor = request.getParameter("doctor");

        if (name != null && !name.isEmpty() &&
                date != null && !date.isEmpty() &&
                diagnosis != null && !diagnosis.isEmpty() &&
                prescription != null && !prescription.isEmpty() &&
                doctor != null && !doctor.isEmpty()) {

            saveMedicalHistory(request, name, date, diagnosis, prescription, doctor);
            response.sendRedirect("patient_medical_history.html");
        } else {
            response.sendRedirect("patient_medical_history.html?error=missing_fields");
        }
    }

    private void saveMedicalHistory(HttpServletRequest request, String name, String date,
                                    String diagnosis, String prescription, String doctor) {
        String filePath = getServletContext().getRealPath(FILE_NAME);

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)))) {
            out.println(name + "," + date + "," + diagnosis + "," + prescription + "," + doctor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        List<String[]> records = new ArrayList<>();

        String filePath = getServletContext().getRealPath(FILE_NAME);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    records.add(data);
                }
            }
        } catch (FileNotFoundException e) {
            out.println("<p>No medical history records found.</p>");
        } catch (IOException e) {
            e.printStackTrace();
            out.println("<p>Error reading medical history file.</p>");
        }

        // Bubble Sort to sort records by date
        for (int i = 0; i < records.size() - 1; i++) {
            for (int j = 0; j < records.size() - i - 1; j++) {
                if (records.get(j)[1].compareTo(records.get(j + 1)[1]) > 0) {
                    String[] temp = records.get(j);
                    records.set(j, records.get(j + 1));
                    records.set(j + 1, temp);
                }
            }
        }

        // Display sorted data
        out.println("<html><head><style>");
        out.println("table { width: 100%; border-collapse: collapse; background: rgba(30,30,50,0.95); border-radius: 10px; overflow: hidden; margin-top: 20px; }");
        out.println("th, td { padding: 14px 18px; text-align: center; border-bottom: 1px solid #444; color: #eee; }");
        out.println("th { background: #2c2c44; color: #61dafb; font-size: 16px; text-transform: uppercase; }");
        out.println("td { background: rgba(50,50,70,0.9); font-size: 15px; }");
        out.println("</style></head><body>");
        out.println("<table>");
        out.println("<tr><th>Name</th><th>Date</th><th>Diagnosis</th><th>Prescription</th><th>Doctor</th></tr>");

        if (records.isEmpty()) {
            out.println("<tr><td colspan='5'>No medical history records found.</td></tr>");
        } else {
            for (String[] record : records) {
                out.println("<tr>");
                for (String item : record) {
                    out.println("<td>" + escapeHtml(item) + "</td>");
                }
                out.println("</tr>");
            }
        }

        out.println("</table></body></html>");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
