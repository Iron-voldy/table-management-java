package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;
import java.util.List;

import com.example.restaurant_table_reservation.model.Order;
import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.service.AdminService;
import com.example.restaurant_table_reservation.service.OrderService;
import com.example.restaurant_table_reservation.service.ReservationService;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.service.UserService;
import com.example.restaurant_table_reservation.utils.ReservationFileUtils;
import com.example.restaurant_table_reservation.utils.TableFileUtils;
import com.example.restaurant_table_reservation.utils.OrderFileUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminService adminService;
    private OrderService orderService;
    private TableService tableService;
    private UserService userService;
    private ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        try {
            // Initialize file utilities
            ReservationFileUtils.initialize(getServletContext());
            TableFileUtils.initialize(getServletContext());
            OrderFileUtils.initialize(getServletContext());

            // Initialize services
            adminService = (AdminService) getServletContext().getAttribute("adminService");
            if (adminService == null) {
                adminService = new AdminService();
                getServletContext().setAttribute("adminService", adminService);
            }

            orderService = (OrderService) getServletContext().getAttribute("orderService");
            if (orderService == null) {
                orderService = new OrderService();
                getServletContext().setAttribute("orderService", orderService);
            }

            tableService = TableService.getInstance();
            userService = UserService.getInstance();
            reservationService = ReservationService.getInstance();

            System.out.println("AdminDashboardServlet initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing AdminDashboardServlet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Failed to initialize AdminDashboardServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null ||
                !(Boolean) session.getAttribute("isAdmin")) {
            System.out.println("Admin access denied - redirecting to login");
            response.sendRedirect("../login.jsp");
            return;
        }

        try {
            // Get data from services
            List<User> users = userService.getAllUsers();
            List<Order> orders = orderService.getAllOrders();
            Table[] tables = tableService.getAllTables();
            List<Reservation> reservations = reservationService.getSortedReservations(); // Get sorted reservations

            // Calculate statistics
            int totalUsers = users.size();
            int todaysOrdersCount = orders.size(); // Simplified - you can enhance this to filter by date
            double todaysRevenue = orders.stream().mapToDouble(Order::getTotalPrice).sum();
            long availableTablesCount = tableService.getAvailableTablesCount();
            int totalTablesCount = tableService.getTotalTablesCount();

            // Set attributes for JSP
            request.setAttribute("users", users);
            request.setAttribute("orders", orders);
            request.setAttribute("tables", tables);
            request.setAttribute("reservations", reservations);
            request.setAttribute("userService", userService);
            request.setAttribute("tableService", tableService);

            // Set summary attributes
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("todaysOrdersCount", todaysOrdersCount);
            request.setAttribute("todaysRevenue", todaysRevenue);
            request.setAttribute("availableTablesCount", availableTablesCount);
            request.setAttribute("totalTablesCount", totalTablesCount);

            // Add reservation statistics
            request.setAttribute("totalReservations", reservations.size());
            request.setAttribute("reservationsSorted", reservationService.isSorted());
            request.setAttribute("sortStatistics", reservationService.getSortStatistics());

            System.out.println("Admin dashboard data prepared successfully");
            request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in AdminDashboardServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null ||
                !(Boolean) session.getAttribute("isAdmin")) {
            response.sendRedirect("../login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("addTable".equals(action)) {
                int number = Integer.parseInt(request.getParameter("number"));
                int capacity = Integer.parseInt(request.getParameter("capacity"));
                String category = request.getParameter("category");
                boolean available = true;

                int newId = tableService.getTotalTablesCount() + 1;
                tableService.addTable(newId, number, capacity, available, category);

                System.out.println("Table added successfully: ID=" + newId + ", Number=" + number);

            } else if ("updateTable".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                int number = Integer.parseInt(request.getParameter("number"));
                int capacity = Integer.parseInt(request.getParameter("capacity"));
                String category = request.getParameter("category");
                boolean available = Boolean.parseBoolean(request.getParameter("available"));

                tableService.updateTable(id, number, capacity, available, category);

                System.out.println("Table updated successfully: ID=" + id);

            } else if ("sortReservations".equals(action)) {
                // Trigger reservation sorting
                reservationService.sortReservationsByTime();
                System.out.println("Reservations sorted by time using merge sort");

            } else if ("demonstrateSort".equals(action)) {
                // Demonstrate merge sort functionality
                reservationService.demonstrateMergeSort();
                System.out.println("Merge sort demonstration completed");
            }

            response.sendRedirect("dashboard");

        } catch (Exception e) {
            System.err.println("Error processing admin dashboard action: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            doGet(request, response);
        }
    }
}