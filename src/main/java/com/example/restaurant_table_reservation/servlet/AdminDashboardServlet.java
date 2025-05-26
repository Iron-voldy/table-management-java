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
        ReservationFileUtils.initialize(getServletContext()); // Initialize ReservationFileUtils
        reservationService = ReservationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null || !(Boolean) session.getAttribute("isAdmin")) {
            response.sendRedirect("../login.jsp");
            return;
        }

        List<User> users = adminService.getAllUsers();
        List<Order> orders = orderService.getAllOrders();
        Table[] tables = tableService.getAllTables();
        List<Reservation> reservations = reservationService.getAllReservations();

        // Calculate total users
        int totalUsers = users.size();

        // Remove today's orders count and today's revenue calculations due to missing order date
        int todaysOrdersCount = orders.size(); // fallback to total orders count
        double todaysRevenue = orders.stream().mapToDouble(Order::getTotalPrice).sum();

        // Calculate available tables count
        long availableTablesCount = tableService.getAvailableTablesCount();
        int totalTablesCount = tableService.getTotalTablesCount();

        request.setAttribute("users", users);
        request.setAttribute("orders", orders);
        request.setAttribute("tables", tables);
        request.setAttribute("reservations", reservations);
        request.setAttribute("userService", userService); // Pass userService to JSP
        request.setAttribute("tableService", tableService); // Pass tableService to JSP

        // Set summary attributes
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("todaysOrdersCount", todaysOrdersCount);
        request.setAttribute("todaysRevenue", todaysRevenue);
        request.setAttribute("availableTablesCount", availableTablesCount);
        request.setAttribute("totalTablesCount", totalTablesCount);

        request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null || !(Boolean) session.getAttribute("isAdmin")) {
            response.sendRedirect("../login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("addTable".equals(action)) {
            int number = Integer.parseInt(request.getParameter("number"));
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String category = request.getParameter("category");
            boolean available = true;

            int newId = tableService.getTotalTablesCount() + 1;
            tableService.addTable(newId, number, capacity, available, category);
        } else if ("updateTable".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            int number = Integer.parseInt(request.getParameter("number"));
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String category = request.getParameter("category");
            boolean available = Boolean.parseBoolean(request.getParameter("available"));

            tableService.updateTable(id, number, capacity, available, category);
        }

        response.sendRedirect("dashboard");
    }
}