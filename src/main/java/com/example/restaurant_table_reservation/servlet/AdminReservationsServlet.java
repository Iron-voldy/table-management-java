package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.Reservation;
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

import java.io.IOException;
import java.util.List;

@WebServlet("/adminReservations")
public class AdminReservationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservationService reservationService;
    private UserService userService;
    private TableService tableService;

    @Override
    public void init() throws ServletException {
        ReservationFileUtils.initialize(getServletContext());
        reservationService = ReservationService.getInstance();
        userService = UserService.getInstance();
        tableService = TableService.getInstance();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null || !(boolean) session.getAttribute("isAdmin")) {
            response.sendRedirect("login.jsp"); // Redirect to login if not admin
            return;
        }

        List<Reservation> reservations = reservationService.getAllReservations();
        request.setAttribute("reservations", reservations);
        request.setAttribute("userService", userService); // Pass userService to JSP for fetching usernames
        request.setAttribute("tableService", tableService); // Pass tableService to JSP for fetching table numbers

        request.getRequestDispatcher("/adminReservations.jsp").forward(request, response);
    }

    // Optional: doPost for handling potential reservation management actions (e.g., cancellation)
} 