package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/editProfile")
public class EditProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        req.setAttribute("user", user);
        req.getRequestDispatcher("/editUserProfile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (int) session.getAttribute("userId");
            
            // Get the current user
            User currentUser = null;
            for (User user : userService.getAllUsers()) {
                if (user.getId() == userId) {
                    currentUser = user;
                    break;
                }
            }

            if (currentUser != null) {
                // Update user information
                currentUser.setUsername(req.getParameter("username"));
                currentUser.setEmail(req.getParameter("email"));
                currentUser.setPhone(req.getParameter("phone"));
                currentUser.setAddress(req.getParameter("address"));
                currentUser.setFirstName(req.getParameter("firstName"));
                currentUser.setLastName(req.getParameter("lastName"));

                // Update password if provided
                String newPassword = req.getParameter("password");
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    currentUser.setPassword(newPassword);
                }

                if (userService.updateUserProfile(currentUser)) {
                    session.setAttribute("user", currentUser);
                    req.setAttribute("message", "Profile updated successfully");
                } else {
                    req.setAttribute("error", "Failed to update profile");
                }
            } else {
                req.setAttribute("error", "User not found");
            }
            
            req.getRequestDispatcher("profile.jsp").forward(req, resp);
        } else {
            resp.sendRedirect("login.jsp");
        }
    }
}