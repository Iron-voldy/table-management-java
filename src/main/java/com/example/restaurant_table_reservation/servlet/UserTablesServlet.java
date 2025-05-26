package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.service.ReservationService;
import com.example.restaurant_table_reservation.utils.TableFileUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/userTables")
public class UserTablesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TableService tableService;
    private ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        TableFileUtils.initialize(getServletContext()); // Initialize file path
        tableService = TableService.getInstance();
        getServletContext().setAttribute("tableService", tableService);
        reservationService = ReservationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get all tables and convert to List for easier handling
        Table[] tablesArray = tableService.getAllTables();
        List<Table> tables = new ArrayList<>();
        for (Table table : tablesArray) {
            if (table != null) {
                tables.add(table);
            }
        }

        request.setAttribute("tables", tables);
        request.getRequestDispatcher("userTables.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("reserve".equals(action)) {
            try {
                int tableId = Integer.parseInt(request.getParameter("tableId"));
                String reservationTime = request.getParameter("reservationTime");
                int userId = (int) session.getAttribute("userId");

                // Get the table
                Table table = tableService.getTableById(tableId);
                if (table != null && table.isAvailable()) {
                    // Update table availability
                    tableService.updateTable(
                            table.getId(),
                            table.getNumber(),
                            table.getCapacity(),
                            false, // Set to not available
                            table.getCategory()
                    );

                    // Add the reservation
                    Reservation newReservation = new Reservation(0, userId, tableId, reservationTime);
                    reservationService.addReservation(newReservation);

                    request.setAttribute("message", "Table reserved successfully!");
                } else {
                    request.setAttribute("error", "Table is no longer available.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid table ID.");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "An error occurred during reservation.");
            }
        }

        // Redirect back to tables view
        response.sendRedirect("userTables");
    }
}