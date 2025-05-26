package com.example.restaurant_table_reservation.utils;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.service.AdminService;
import com.example.restaurant_table_reservation.service.UserService;
import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Initialize file paths
            UserFileUtils.initialize(sce.getServletContext());
            FileUtils.initialize(sce.getServletContext());

            // Ensure users.json directory and file exist
            String realPath = sce.getServletContext().getRealPath("/WEB-INF/data/");
            File directory = new File(realPath);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Created directory: " + realPath);
                } else {
                    System.err.println("Failed to create directory: " + realPath);
                    return;
                }
            }

            // Check if users.json has a default admin; if not, create one
            String userData = UserFileUtils.readFile();
            Gson gson = new Gson();
            List<User> users = gson.fromJson(userData, new com.google.gson.reflect.TypeToken<List<User>>(){}.getType());
            if (users == null || users.isEmpty()) {
                users = new ArrayList<>();
                User defaultAdmin = new User(1, "admin", "admin", "admin@example.com", "admin", "admin", true);
                users.add(defaultAdmin);
                UserFileUtils.writeFile(gson.toJson(users));
                System.out.println("Created default admin user: admin/admin");
            } else {
                boolean hasAdmin = false;
                for (User user : users) {
                    if (user.isAdmin()) {
                        hasAdmin = true;
                        break;
                    }
                }
                if (!hasAdmin) {
                    User defaultAdmin = new User(users.size() + 1, "admin", "admin", "admin@example.com", "admin", "admin", true);
                    users.add(defaultAdmin);
                    UserFileUtils.writeFile(gson.toJson(users));
                    System.out.println("Added default admin user: admin/admin");
                }
            }

            // Initialize services using singleton pattern
            UserService userService = UserService.getInstance();
            sce.getServletContext().setAttribute("userService", userService);

            AdminService adminService = new AdminService();
            sce.getServletContext().setAttribute("adminService", adminService);

            System.out.println("Application initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
        System.out.println("Application context destroyed");
    }
}