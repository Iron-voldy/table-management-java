package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.service.TableService;
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

    @Override
    public void init() throws ServletException {
        tableService = TableService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            String reservationTimeStr = request.getParameter("reservationTime");

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

            // Update table availability
            tableService.updateTable(
                table.getId(),
                table.getNumber(),
                table.getCapacity(),
                false, // Set to not available
                table.getCategory()
            );

            request.setAttribute("message", "Table reserved successfully for " + 
                reservationTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
            response.sendRedirect("userTables");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid table ID");
            request.getRequestDispatcher("userTables.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error processing reservation: " + e.getMessage());
            request.getRequestDispatcher("userTables.jsp").forward(request, response);
        }
    }
}