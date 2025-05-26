package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("RegistrationServlet: GET request received at " + new java.util.Date());
        req.getRequestDispatcher("/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("RegistrationServlet: POST request received at " + new java.util.Date());
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        System.out.println("Registration attempt - Username: " + username + ", Email: " + email);

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            System.out.println("Registration failed: Missing or empty parameters");
            request.setAttribute("error", "Please fill in all required fields");
            request.getRequestDispatcher("registration.jsp").forward(request, response);
            return;
        }

        if (userService.registerUser(username, password, email)) {
            System.out.println("Registration successful for user: " + username);
            HttpSession session = request.getSession();
            session.setAttribute("message", "Registration successful! Please login.");
            response.sendRedirect("login.jsp");
        } else {
            System.out.println("Registration failed for user: " + username + " - Username may be taken");
            request.setAttribute("error", "Username already exists. Please choose a different username.");
            request.getRequestDispatcher("registration.jsp").forward(request, response);
        }
    }
}