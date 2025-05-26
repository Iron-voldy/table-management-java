package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.service.UserService;
import com.example.restaurant_table_reservation.utils.ReservationQueue;
import com.example.restaurant_table_reservation.utils.QueueFileUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/reservationQueue")
public class ReservationQueueServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservationQueue reservationQueue;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        // Initialize file utilities
        QueueFileUtils.initialize(getServletContext());

        // Initialize queue with max size of 100
        reservationQueue = ReservationQueue.getInstance(100);

        // Initialize user service
        userService = UserService.getInstance();

        // Store in servlet context for other servlets to use
        getServletContext().setAttribute("reservationQueue", reservationQueue);

        System.out.println("ReservationQueueServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get all reservations in queue
        List<Reservation> queuedReservations = reservationQueue.getAllReservationsList();

        // Get queue statistics
        String queueStats = reservationQueue.getQueueStatistics();

        // Set attributes for JSP
        request.setAttribute("queuedReservations", queuedReservations);
        request.setAttribute("queueStats", queueStats);
        request.setAttribute("queueSize", reservationQueue.getSize());
        request.setAttribute("queueEmpty", reservationQueue.isEmpty());
        request.setAttribute("queueFull", reservationQueue.isFull());

        // Forward to JSP
        request.getRequestDispatcher("reservationQueue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "addToQueue":
                    handleAddToQueue(request, response);
                    break;
                case "removeFromQueue":
                    handleRemoveFromQueue(request, response);
                    break;
                case "processNext":
                    handleProcessNext(request, response);
                    break;
                case "clearQueue":
                    handleClearQueue(request, response);
                    break;
                default:
                    request.setAttribute("error", "Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing queue action: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
        }

        // Redirect back to queue view
        response.sendRedirect("reservationQueue");
    }

    private void handleAddToQueue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");

        // Get parameters
        String tableIdStr = request.getParameter("tableId");
        String reservationTime = request.getParameter("reservationTime");
        String customerName = request.getParameter("customerName");
        String customerPhone = request.getParameter("customerPhone");

        // Validate parameters
        if (tableIdStr == null || reservationTime == null || customerName == null) {
            request.setAttribute("error", "Missing required fields");
            return;
        }

        try {
            int tableId = Integer.parseInt(tableIdStr);

            // Validate reservation time is in the future
            LocalDateTime requestedTime = LocalDateTime.parse(reservationTime,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            if (requestedTime.isBefore(LocalDateTime.now())) {
                request.setAttribute("error", "Cannot make reservations for past times");
                return;
            }

            // Create new reservation
            Reservation reservation = new Reservation();
            reservation.setUserId(userId);
            reservation.setTableId(tableId);
            reservation.setReservationTime(reservationTime);
            reservation.setCustomerName(customerName);
            reservation.setCustomerPhone(customerPhone);
            reservation.setStatus("pending");

            // Add to queue
            if (reservationQueue.enqueue(reservation)) {
                request.setAttribute("message", "Reservation added to queue successfully!");
                System.out.println("Reservation added to queue: " + reservation);
            } else {
                request.setAttribute("error", "Failed to add reservation to queue. Queue may be full.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid table ID");
        } catch (Exception e) {
            request.setAttribute("error", "Error processing reservation: " + e.getMessage());
        }
    }

    private void handleRemoveFromQueue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationIdStr = request.getParameter("reservationId");

        if (reservationIdStr == null) {
            request.setAttribute("error", "Missing reservation ID");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdStr);

            if (reservationQueue.removeReservationById(reservationId)) {
                request.setAttribute("message", "Reservation removed from queue successfully!");
            } else {
                request.setAttribute("error", "Reservation not found in queue");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid reservation ID");
        }
    }

    private void handleProcessNext(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Reservation nextReservation = reservationQueue.dequeue();

        if (nextReservation != null) {
            // Mark as confirmed
            nextReservation.confirm();
            request.setAttribute("message", "Processed reservation: " + nextReservation.getCustomerName());
            System.out.println("Processed reservation from queue: " + nextReservation);
        } else {
            request.setAttribute("error", "No reservations in queue to process");
        }
    }

    private void handleClearQueue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Clear all reservations from queue
        while (!reservationQueue.isEmpty()) {
            reservationQueue.dequeue();
        }

        request.setAttribute("message", "Queue cleared successfully!");
        System.out.println("Reservation queue cleared");
    }

    // Utility method to get user by ID
    private User getUserById(int userId) {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }
}