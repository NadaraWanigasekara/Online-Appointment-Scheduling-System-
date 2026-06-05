package com.example.mywebapp;

import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AdminManageDoctorsServlet")
public class AdminManageDoctorsServlet extends HttpServlet {
    private static final String FILE_NAME = "/WEB-INF/doctors.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String doctorId = request.getParameter("doctorId");
        String name = request.getParameter("name");
        String specialization = request.getParameter("specialization");
        String experience = request.getParameter("experience");

        if (isNotEmpty(doctorId) && isNotEmpty(name) && isNotEmpty(specialization) && isNotEmpty(experience)) {
            saveDoctor(request, doctorId, name, specialization, experience);
            response.sendRedirect("admin_manageDoctors.html");
        } else {
            response.sendRedirect("admin_manageDoctors.html?error=missing_fields");
        }
    }

    private void saveDoctor(HttpServletRequest request, String doctorId, String name, String specialization, String experience) {
        String filePath = getServletContext().getRealPath(FILE_NAME);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)))) {
            out.println(doctorId + "," + name + "," + specialization + "," + experience);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        List<String[]> doctors = new ArrayList<>();

        String filePath = getServletContext().getRealPath(FILE_NAME);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    doctors.add(data);
                }
            }
        } catch (FileNotFoundException e) {
            out.println("<p>No doctor records found.</p>");
        } catch (IOException e) {
            e.printStackTrace();
            out.println("<p>Error loading doctor records.</p>");
        }

        // Bubble Sort to sort doctors by experience (Descending Order)
        for (int i = 0; i < doctors.size() - 1; i++) {
            for (int j = 0; j < doctors.size() - i - 1; j++) {
                int experience1 = Integer.parseInt(doctors.get(j)[3]);
                int experience2 = Integer.parseInt(doctors.get(j + 1)[3]);

                if (experience1 < experience2) {
                    String[] temp = doctors.get(j);
                    doctors.set(j, doctors.get(j + 1));
                    doctors.set(j + 1, temp);
                }
            }
        }

        // Display sorted doctor records
        out.println("<html><head><style>");
        out.println("table { width: 100%; border-collapse: collapse; background: rgba(30,30,50,0.95); border-radius: 10px; overflow: hidden; margin-top: 20px; }");
        out.println("th, td { padding: 14px 18px; text-align: center; border-bottom: 1px solid #444; color: #eee; }");
        out.println("th { background: #2c2c44; color: #61dafb; font-size: 16px; text-transform: uppercase; }");
        out.println("td { background: rgba(50,50,70,0.9); font-size: 15px; }");
        out.println("</style></head><body>");
        out.println("<table><tr><th>ID</th><th>Name</th><th>Specialization</th><th>Experience</th></tr>");

        if (doctors.isEmpty()) {
            out.println("<tr><td colspan='4'>No doctor records available.</td></tr>");
        } else {
            for (String[] doctor : doctors) {
                out.println("<tr>");
                for (String item : doctor) {
                    out.println("<td>" + escapeHtml(item) + "</td>");
                }
                out.println("</tr>");
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
                .replace("'", "&#x27;");
    }
}
