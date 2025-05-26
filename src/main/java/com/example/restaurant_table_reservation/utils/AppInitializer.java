package com.example.restaurant_table_reservation.utils;

import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.service.AdminService;
import com.example.restaurant_table_reservation.service.UserService;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.service.ReservationService;
import com.example.restaurant_table_reservation.service.OrderService;
import com.example.restaurant_table_reservation.service.ReviewService;
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
            System.out.println("=== Restaurant Table Reservation System Initialization Started ===");
            ServletContext context = sce.getServletContext();

            // Step 1: Initialize all file utilities
            initializeFileUtils(context);

            // Step 2: Initialize default data
            initializeDefaultUsers(context);
            initializeDefaultTables(context);

            // Step 3: Initialize all services
            initializeServices(context);

            System.out.println("=== Application Initialization Completed Successfully ===");
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Application initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize all file utilities for data persistence
     */
    private void initializeFileUtils(ServletContext context) {
        try {
            System.out.println("🔧 Initializing file utilities...");

            // Initialize all file utilities
            UserFileUtils.initialize(context);
            ReservationFileUtils.initialize(context);
            TableFileUtils.initialize(context);
            OrderFileUtils.initialize(context);
            ReviewFileUtils.initialize(context);
            QueueFileUtils.initialize(context);
            FileUtils.initialize(context); // For menu items

            System.out.println("✅ File utilities initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing file utilities: " + e.getMessage());
            throw new RuntimeException("Failed to initialize file utilities", e);
        }
    }

    /**
     * Initialize default users including admin account
     */
    private void initializeDefaultUsers(ServletContext context) {
        try {
            System.out.println("👤 Initializing default users...");

            String userData = UserFileUtils.readFile();
            Gson gson = new Gson();
            List<User> users = gson.fromJson(userData, new com.google.gson.reflect.TypeToken<List<User>>(){}.getType());

            if (users == null || users.isEmpty()) {
                users = new ArrayList<>();
                // Create default admin with correct constructor order
                User defaultAdmin = new User(1, "admin", "admin@example.com", "admin", "", "", true);
                users.add(defaultAdmin);
                UserFileUtils.writeFile(gson.toJson(users));
                System.out.println("✅ Created default admin user: admin/admin");
            } else {
                boolean hasAdmin = users.stream().anyMatch(User::isAdmin);
                if (!hasAdmin) {
                    int nextId = users.stream().mapToInt(User::getId).max().orElse(0) + 1;
                    User defaultAdmin = new User(nextId, "admin", "admin@example.com", "admin", "", "", true);
                    users.add(defaultAdmin);
                    UserFileUtils.writeFile(gson.toJson(users));
                    System.out.println("✅ Added default admin user: admin/admin");
                }
            }

            System.out.println("✅ Default users initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing default users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize default tables for the restaurant
     */
    private void initializeDefaultTables(ServletContext context) {
        try {
            System.out.println("🪑 Initializing default tables...");

            String tableData = TableFileUtils.readTablesFile();
            Gson gson = new Gson();
            Table[] tables = gson.fromJson(tableData, Table[].class);

            if (tables == null || tables.length == 0) {
                // Create default tables with different categories
                Table[] defaultTables = {
                        new Table(1, 1, 2, true, "Window"),
                        new Table(2, 2, 4, true, "Standard"),
                        new Table(3, 3, 6, true, "VIP"),
                        new Table(4, 4, 4, true, "Standard"),
                        new Table(5, 5, 8, true, "Large"),
                        new Table(6, 6, 2, true, "Window"),
                        new Table(7, 7, 4, true, "Standard"),
                        new Table(8, 8, 6, true, "VIP")
                };

                TableFileUtils.writeTablesFile(gson.toJson(defaultTables));
                System.out.println("✅ Created default tables: " + defaultTables.length + " tables");
            } else {
                System.out.println("✅ Tables already exist: " + tables.length + " tables found");
            }

            System.out.println("✅ Default tables initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing default tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize all services and register them in ServletContext
     */
    private void initializeServices(ServletContext context) {
        try {
            System.out.println("🔄 Initializing services...");

            // Initialize UserService singleton
            UserService userService = UserService.getInstance();
            context.setAttribute("userService", userService);
            System.out.println("✅ UserService initialized");

            // Initialize AdminService
            AdminService adminService = new AdminService();
            context.setAttribute("adminService", adminService);
            System.out.println("✅ AdminService initialized");

            // Initialize TableService singleton
            TableService tableService = TableService.getInstance();
            context.setAttribute("tableService", tableService);
            System.out.println("✅ TableService initialized");

            // Initialize ReservationService singleton
            ReservationService reservationService = ReservationService.getInstance();
            context.setAttribute("reservationService", reservationService);
            System.out.println("✅ ReservationService initialized");

            // Initialize OrderService
            OrderService orderService = new OrderService();
            context.setAttribute("orderService", orderService);
            System.out.println("✅ OrderService initialized");

            // Initialize ReviewService
            ReviewService reviewService = new ReviewService();
            context.setAttribute("reviewService", reviewService);
            System.out.println("✅ ReviewService initialized");

            // Initialize ReservationQueue
            ReservationQueue reservationQueue = ReservationQueue.getInstance(100);
            context.setAttribute("reservationQueue", reservationQueue);
            System.out.println("✅ ReservationQueue initialized");

            System.out.println("✅ All services initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing services: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize empty JSON files if they don't exist
     */
    private void initializeEmptyJsonFiles() {
        try {
            System.out.println("📄 Checking and initializing empty JSON files...");

            // Initialize empty files with proper JSON structure
            String emptyArrayJson = "[]";

            // Check and create reservations.json
            String reservationData = ReservationFileUtils.readReservationsFile();
            if (reservationData.trim().isEmpty()) {
                ReservationFileUtils.writeReservationsFile(emptyArrayJson);
                System.out.println("✅ Initialized empty reservations.json");
            }

            // Check and create orders.json
            String orderData = OrderFileUtils.readFile();
            if (orderData.trim().isEmpty()) {
                OrderFileUtils.writeFile(emptyArrayJson);
                System.out.println("✅ Initialized empty orders.json");
            }

            // Check and create review.json
            String reviewData = ReviewFileUtils.readFile();
            if (reviewData.trim().isEmpty()) {
                ReviewFileUtils.writeFile(emptyArrayJson);
                System.out.println("✅ Initialized empty review.json");
            }

            // Check and create menu.json
            String menuData = FileUtils.readFile();
            if (menuData.trim().isEmpty()) {
                FileUtils.writeFile(emptyArrayJson);
                System.out.println("✅ Initialized empty menu.json");
            }

            System.out.println("✅ JSON files initialization completed");
        } catch (Exception e) {
            System.err.println("❌ Error initializing JSON files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Print system information for debugging
     */
    private void printSystemInfo(ServletContext context) {
        System.out.println("\n=== System Information ===");
        System.out.println("📁 Context Path: " + context.getContextPath());
        System.out.println("🏠 Real Path: " + context.getRealPath("/"));
        System.out.println("🔧 Server Info: " + context.getServerInfo());
        System.out.println("☕ Java Version: " + System.getProperty("java.version"));
        System.out.println("💻 OS: " + System.getProperty("os.name"));
        System.out.println("🕒 Startup Time: " + new java.util.Date());
        System.out.println("===============================\n");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            System.out.println("=== Application Shutdown Started ===");

            // Cleanup resources if needed
            ServletContext context = sce.getServletContext();

            // Remove services from context
            context.removeAttribute("userService");
            context.removeAttribute("adminService");
            context.removeAttribute("tableService");
            context.removeAttribute("reservationService");
            context.removeAttribute("orderService");
            context.removeAttribute("reviewService");
            context.removeAttribute("reservationQueue");

            System.out.println("✅ Services cleaned up");
            System.out.println("=== Application Shutdown Completed ===");
        } catch (Exception e) {
            System.err.println("❌ Error during application shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validate that all critical services are working
     */
    private void validateServices(ServletContext context) {
        try {
            System.out.println("🔍 Validating services...");

            // Test UserService
            UserService userService = (UserService) context.getAttribute("userService");
            if (userService != null) {
                List<User> users = userService.getAllUsers();
                System.out.println("✅ UserService: " + users.size() + " users loaded");
            }

            // Test TableService
            TableService tableService = (TableService) context.getAttribute("tableService");
            if (tableService != null) {
                Table[] tables = tableService.getAllTables();
                System.out.println("✅ TableService: " + tables.length + " tables loaded");
            }

            // Test ReservationService
            ReservationService reservationService = (ReservationService) context.getAttribute("reservationService");
            if (reservationService != null) {
                List<com.example.restaurant_table_reservation.model.Reservation> reservations = reservationService.getAllReservations();
                System.out.println("✅ ReservationService: " + reservations.size() + " reservations loaded");
            }

            System.out.println("✅ Service validation completed");
        } catch (Exception e) {
            System.err.println("❌ Service validation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}