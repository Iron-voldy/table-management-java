package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.service.ReservationService;
import com.example.restaurant_table_reservation.utils.ReservationFileUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/reserve")
public class ReservationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TableService tableService;
    private ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        // Initialize file utilities
        ReservationFileUtils.initialize(getServletContext());

        // Initialize services
        tableService = TableService.getInstance();
        reservationService = ReservationService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Get parameters
            String tableIdStr = request.getParameter("tableId");
            String reservationTimeStr = request.getParameter("reservationTime");

            if (tableIdStr == null || reservationTimeStr == null) {
                request.setAttribute("error", "Missing required parameters");
                request.getRequestDispatcher("userTables.jsp").forward(request, response);
                return;
            }

            int tableId = Integer.parseInt(tableIdStr);
            int userId = (int) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");

            // Validate reservation time
            LocalDateTime reservationTime = LocalDateTime.parse(reservationTimeStr,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime now = LocalDateTime.now();

            if (reservationTime.isBefore(now)) {
                request.setAttribute("error", "Cannot make reservations for past times");
                request.getRequestDispatcher("userTables.jsp").forward(request, response);
                return;
            }

            // Get the table
            Table table = tableService.getTableById(tableId);
            if (table == null) {
                request.setAttribute("error", "Table not found");
                request.getRequestDispatcher("userTables.jsp").forward(request, response);
                return;
            }

            if (!table.isAvailable()) {
                request.setAttribute("error", "Table is no longer available");
                request.getRequestDispatcher("userTables.jsp").forward(request, response);
                return;
            }

            // Create reservation
            Reservation newReservation = new Reservation();
            newReservation.setUserId(userId);
            newReservation.setTableId(tableId);
            newReservation.setReservationTime(reservationTimeStr);
            newReservation.setCustomerName(username);
            newReservation.setStatus("confirmed");

            // Add reservation to service (this will trigger merge sort)
            reservationService.addReservation(newReservation);
            System.out.println("Reservation added successfully: " + newReservation);

            // Update table availability
            tableService.updateTable(
                    table.getId(),
                    table.getNumber(),
                    table.getCapacity(),
                    false, // Set to not available
                    table.getCategory()
            );

            // Set success message
            String formattedTime = reservationTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
            request.setAttribute("message", "Table " + table.getNumber() + " reserved successfully for " + formattedTime);

            System.out.println("Table reservation completed successfully");
            response.sendRedirect("userTables");

        } catch (NumberFormatException e) {
            System.err.println("Invalid table ID format: " + e.getMessage());
            request.setAttribute("error", "Invalid table ID");
            request.getRequestDispatcher("userTables.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error processing reservation: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing reservation: " + e.getMessage());
            request.getRequestDispatcher("userTables.jsp").forward(request, response);
        }
    }
}