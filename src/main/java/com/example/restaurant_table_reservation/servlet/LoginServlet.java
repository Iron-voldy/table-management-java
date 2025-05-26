package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // If user is already logged in, redirect to appropriate page
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
                response.sendRedirect("admin/dashboard");
            } else {
                response.sendRedirect("userTables");
            }
            return;
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter both username and password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        User user = userService.authenticateUser(username, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("isAdmin", user.isAdmin());

            if (user.isAdmin()) {
                response.sendRedirect("admin/dashboard");
            } else {
                response.sendRedirect("userTables");
            }
        } else {
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}