package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = (AdminService) getServletContext().getAttribute("adminService");
        if (adminService == null) {
            adminService = new AdminService();
            getServletContext().setAttribute("adminService", adminService);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String adminName = req.getParameter("username");
        String adminPassword = req.getParameter("password");

        if (adminName == null || adminPassword == null) {
            req.setAttribute("error", "Username and password are required");
            req.getRequestDispatcher("adminLogin.jsp").forward(req, resp);
            return;
        }

        adminName = adminName.trim();
        adminPassword = adminPassword.trim();
        System.out.println("Admin login attempt - Username: " + adminName + ", Password: " + adminPassword);

        User admin = adminService.authenticateAdmin(adminName, adminPassword);
        if (admin != null) {
            HttpSession session = req.getSession();
            session.setAttribute("user", admin);
            session.setAttribute("userId", admin.getId());
            session.setAttribute("username", admin.getUsername());
            session.setAttribute("isAdmin", true);
            session.setAttribute("adminUser", admin);

            System.out.println("Admin login successful - redirecting to dashboard");

            // Fixed redirect path - use relative path
            String contextPath = req.getContextPath();
            resp.sendRedirect(contextPath + "/adminDashboard");

        } else {
            System.out.println("Admin login failed - invalid credentials");
            req.setAttribute("error", "Invalid admin username or password");
            req.getRequestDispatcher("adminLogin.jsp").forward(req, resp);
        }
    }
}