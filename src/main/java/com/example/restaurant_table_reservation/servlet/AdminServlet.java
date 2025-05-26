package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.utils.Queue;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.utils.ReservationFileUtils;
import com.example.restaurant_table_reservation.utils.TableFileUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/adminDashboard")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Queue reservationQueue;
    private TableService tableService;

    @Override
    public void init() throws ServletException {
        ReservationFileUtils.initialize(getServletContext());
        TableFileUtils.initialize(getServletContext());
        reservationQueue = Queue.getInstance(100);
        tableService = TableService.getInstance();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null || !(Boolean) session.getAttribute("isAdmin")) {
            response.sendRedirect("login?error=adminAccessDenied");
            return;
        }

        // Populate dashboard attributes
        // TODO: Replace with actual logic to fetch total users
        request.setAttribute("totalUsers", 100);
        // TODO: Replace with actual logic to fetch today's orders count
        request.setAttribute("todaysOrdersCount", 20);
        request.setAttribute("availableTablesCount", tableService.getAvailableTablesCount());
        request.setAttribute("totalTablesCount", tableService.getTotalTablesCount());
        // TODO: Replace with actual logic to fetch today's revenue
        request.setAttribute("todaysRevenue", 1500.50);
        // TODO: Replace with actual logic to fetch users
        List<User> users = List.of(new User(1, "user1", "user1@example.com"));
        request.setAttribute("users", users);

        // Fetch reservations
        List<Reservation> reservations = reservationQueue.getReservations();
        request.setAttribute("reservations", reservations);

        request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response);
    }
}