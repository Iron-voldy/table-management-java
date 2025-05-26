package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;
import java.util.List;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.service.AdminService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ShowUsersServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("adminUser") == null) {
            resp.sendRedirect("adminLogin.jsp");
            return;
        }
        List<User> users = adminService.getAllUsers();
        req.setAttribute("users", users);
        req.getRequestDispatcher("showUsers.jsp").forward(req, resp);
    }
}