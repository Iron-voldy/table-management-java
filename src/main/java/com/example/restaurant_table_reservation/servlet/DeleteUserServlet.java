package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/DeleteUserServlet")
public class DeleteUserServlet extends HttpServlet {
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
        String idStr = req.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            adminService.deleteUser(id);
        }
        resp.sendRedirect("showUsers");
    }
}