package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.service.AdminService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/EditUserServlet")
public class EditUserServlet extends HttpServlet {
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
        if (idStr == null) {
            resp.sendRedirect("showUsers");
            return;
        }
        int id = Integer.parseInt(idStr);
        User user = null;
        for (User u : adminService.getAllUsers()) {
            if (u.getId() == id) {
                user = u;
                break;
            }
        }
        if (user == null) {
            resp.sendRedirect("showUsers");
            return;
        }
        req.setAttribute("user", user);
        req.getRequestDispatcher("/editUser.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        int id = Integer.parseInt(req.getParameter("id"));
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        User updatedUser = null;
        for (User user : adminService.getAllUsers()) {
            if (user.getId() == id) {
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                updatedUser = user;
                break;
            }
        }
        if (updatedUser != null) {
            if (adminService.updateUser(updatedUser)) {
                resp.sendRedirect("showUsers?update=success");
            } else {
                req.setAttribute("errorMessage", "Username already taken");
                req.setAttribute("user", updatedUser);
                req.getRequestDispatcher("/editUser.jsp").forward(req, resp);
            }
        } else {
            resp.sendRedirect("showUsers?update=failed");
        }
    }
}