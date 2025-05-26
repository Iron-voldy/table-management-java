package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.service.UserService;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.service.ReservationService;
import com.example.restaurant_table_reservation.utils.ReservationQueue;
import com.example.restaurant_table_reservation.utils.MergeSort;
import com.example.restaurant_table_reservation.utils.QueueFileUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/queueManagement")
public class AdminQueueManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservationQueue reservationQueue;
    private UserService userService;
    private TableService tableService;
    private ReservationService reservationService;
    private MergeSort mergeSort;

    @Override
    public void init() throws ServletException {
        System.out.println("AdminQueueManagementServlet: Initializing...");

        // Initialize file utilities
        QueueFileUtils.initialize(getServletContext());

        // Initialize services and queue
        reservationQueue = ReservationQueue.getInstance(100);
        userService = UserService.getInstance();
        tableService = TableService.getInstance();
        reservationService = ReservationService.getInstance();
        mergeSort = new MergeSort();

        // Store in servlet context
        getServletContext().setAttribute("reservationQueue", reservationQueue);

        System.out.println("AdminQueueManagementServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null ||
                !(Boolean) session.getAttribute("isAdmin")) {
            System.out.println("AdminQueueManagementServlet: Non-admin access denied");
            response.sendRedirect("../login.jsp");
            return;
        }

        System.out.println("AdminQueueManagementServlet: Loading queue management page");

        // Get all reservations from both queue and main service
        List<Reservation> queuedReservations = reservationQueue.getAllReservationsList();
        List<Reservation> allReservations = reservationService.getAllReservations();

        // If queue is empty but we have reservations, populate queue
        if (queuedReservations.isEmpty() && !allReservations.isEmpty()) {
            System.out.println("AdminQueueManagementServlet: Populating queue from reservations");
            for (Reservation reservation : allReservations) {
                if ("pending".equals(reservation.getStatus())) {
                    reservationQueue.enqueue(reservation);
                }
            }
            queuedReservations = reservationQueue.getAllReservationsList();
        }

        // Get queue statistics
        String queueStats = reservationQueue.getQueueStatistics();

        // Get all users and tables for reference
        List<User> allUsers = userService.getAllUsers();
        Table[] allTables = tableService.getAllTables();

        // Prepare detailed reservation information
        for (Reservation reservation : queuedReservations) {
            // Find user information
            for (User user : allUsers) {
                if (user.getId() == reservation.getUserId()) {
                    if (reservation.getCustomerName() == null || reservation.getCustomerName().isEmpty()) {
                        reservation.setCustomerName(user.getUsername());
                    }
                    break;
                }
            }
        }

        // Set attributes for JSP
        request.setAttribute("queuedReservations", queuedReservations);
        request.setAttribute("queueStats", queueStats);
        request.setAttribute("queueSize", reservationQueue.getSize());
        request.setAttribute("queueEmpty", reservationQueue.isEmpty());
        request.setAttribute("queueFull", reservationQueue.isFull());
        request.setAttribute("allUsers", allUsers);
        request.setAttribute("allTables", allTables);
        request.setAttribute("userService", userService);
        request.setAttribute("tableService", tableService);

        System.out.println("AdminQueueManagementServlet: Forwarding to JSP with " + queuedReservations.size() + " reservations");

        // Forward to admin queue management JSP
        request.getRequestDispatcher("/queueManagement.jsp").forward(request, response);
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
        System.out.println("AdminQueueManagementServlet: Processing action: " + action);

        try {
            switch (action) {
                case "confirmReservation":
                    handleConfirmReservation(request, response);
                    break;
                case "cancelReservation":
                    handleCancelReservation(request, response);
                    break;
                case "processNext":
                    handleProcessNext(request, response);
                    break;
                case "reorderQueue":
                    handleReorderQueue(request, response);
                    break;
                case "clearQueue":
                    handleClearQueue(request, response);
                    break;
                case "viewStatistics":
                    handleViewStatistics(request, response);
                    break;
                case "demonstrateSort":
                    handleDemonstrateSort(request, response);
                    break;
                case "sortReservations":
                    handleSortReservations(request, response);
                    break;
                default:
                    request.setAttribute("error", "Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing admin queue action: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
        }

        // Redirect back to queue management
        response.sendRedirect("queueManagement");
    }

    private void handleConfirmReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationIdStr = request.getParameter("reservationId");
        System.out.println("AdminQueueManagementServlet: Confirming reservation: " + reservationIdStr);

        if (reservationIdStr == null) {
            request.setAttribute("error", "Missing reservation ID");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdStr);

            // Find reservation in queue
            List<Reservation> reservations = reservationQueue.getAllReservationsList();
            for (Reservation reservation : reservations) {
                if (reservation.getId() == reservationId) {
                    reservation.confirm();
                    // Update in main service too
                    reservationService.updateReservation(reservation);
                    // Remove from queue after confirmation
                    reservationQueue.removeReservationById(reservationId);
                    request.setAttribute("message",
                            "Reservation confirmed for " + reservation.getCustomerName());
                    System.out.println("Admin confirmed reservation: " + reservation);
                    return;
                }
            }

            request.setAttribute("error", "Reservation not found in queue");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid reservation ID");
        }
    }

    private void handleCancelReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationIdStr = request.getParameter("reservationId");
        System.out.println("AdminQueueManagementServlet: Cancelling reservation: " + reservationIdStr);

        if (reservationIdStr == null) {
            request.setAttribute("error", "Missing reservation ID");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdStr);

            // Find and cancel reservation
            List<Reservation> reservations = reservationQueue.getAllReservationsList();
            for (Reservation reservation : reservations) {
                if (reservation.getId() == reservationId) {
                    reservation.cancel();
                    // Update in main service too
                    reservationService.updateReservation(reservation);
                    // Remove from queue after cancellation
                    reservationQueue.removeReservationById(reservationId);
                    request.setAttribute("message",
                            "Reservation cancelled for " + reservation.getCustomerName());
                    System.out.println("Admin cancelled reservation: " + reservation);
                    return;
                }
            }

            request.setAttribute("error", "Reservation not found in queue");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid reservation ID");
        }
    }

    private void handleProcessNext(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AdminQueueManagementServlet: Processing next reservation in queue");

        Reservation nextReservation = reservationQueue.dequeue();

        if (nextReservation != null) {
            nextReservation.confirm();
            // Update in main service
            reservationService.updateReservation(nextReservation);
            request.setAttribute("message",
                    "Processed next reservation: " + nextReservation.getCustomerName());
            System.out.println("Admin processed next reservation: " + nextReservation);
        } else {
            request.setAttribute("error", "No reservations in queue to process");
        }
    }

    private void handleReorderQueue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AdminQueueManagementServlet: Reordering queue using merge sort");

        // Get all reservations from queue
        Reservation[] reservations = reservationQueue.getAllReservations();

        if (reservations.length == 0) {
            request.setAttribute("error", "Queue is empty, nothing to reorder");
            return;
        }

        // Clear the queue
        while (!reservationQueue.isEmpty()) {
            reservationQueue.dequeue();
        }

        // Sort reservations using merge sort
        MergeSort.SortStatistics stats = mergeSort.sortWithStatistics(reservations);

        // Re-add sorted reservations to queue
        for (Reservation reservation : reservations) {
            if (reservation != null) {
                reservationQueue.enqueue(reservation);
            }
        }

        request.setAttribute("message",
                String.format("Queue reordered successfully using Merge Sort! %s", stats.toString()));
        System.out.println("Admin reordered queue: " + stats);
    }

    private void handleClearQueue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AdminQueueManagementServlet: Clearing queue");

        int clearedCount = reservationQueue.getSize();

        // Clear all reservations from queue
        while (!reservationQueue.isEmpty()) {
            reservationQueue.dequeue();
        }

        request.setAttribute("message",
                String.format("Queue cleared successfully! Removed %d reservations.", clearedCount));
        System.out.println("Admin cleared reservation queue: " + clearedCount + " reservations removed");
    }

    private void handleViewStatistics(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AdminQueueManagementServlet: Generating statistics");

        // Generate comprehensive statistics
        List<Reservation> reservations = reservationQueue.getAllReservationsList();

        int totalReservations = reservations.size();
        int pendingReservations = 0;
        int confirmedReservations = 0;
        int cancelledReservations = 0;

        for (Reservation reservation : reservations) {
            switch (reservation.getStatus()) {
                case "pending":
                    pendingReservations++;
                    break;
                case "confirmed":
                    confirmedReservations++;
                    break;
                case "cancelled":
                    cancelledReservations++;
                    break;
            }
        }

        // Check if queue is sorted
        Reservation[] reservationArray = reservationQueue.getAllReservations();
        boolean isSorted = mergeSort.isSorted(reservationArray);

        String statisticsMessage = String.format(
                "Queue Statistics - Total: %d, Pending: %d, Confirmed: %d, Cancelled: %d, Sorted: %s",
                totalReservations, pendingReservations, confirmedReservations, cancelledReservations,
                isSorted ? "Yes" : "No"
        );

        request.setAttribute("message", statisticsMessage);
        System.out.println("Queue statistics: " + statisticsMessage);
    }

    private void handleDemonstrateSort(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AdminQueueManagementServlet: Demonstrating merge sort");

        // Use the reservation service to demonstrate sort
        reservationService.demonstrateMergeSort();

        request.setAttribute("message",
                "Merge Sort demonstration completed! Check server logs for detailed output.");
    }

    private void handleSortReservations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AdminQueueManagementServlet: Sorting all reservations using merge sort");

        // Sort reservations in the main service
        reservationService.sortReservationsByTime();

        // Also sort the queue
        handleReorderQueue(request, response);

        request.setAttribute("message",
                "All reservations sorted by time using Merge Sort algorithm!");
        System.out.println("All reservations sorted successfully");
    }
}