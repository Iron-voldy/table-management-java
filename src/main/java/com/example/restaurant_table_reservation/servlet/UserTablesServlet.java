package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.service.ReservationService;
import com.example.restaurant_table_reservation.utils.TableFileUtils;
import com.example.restaurant_table_reservation.utils.ReservationFileUtils;

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
        try {
            System.out.println("UserTablesServlet: Initializing...");

            // Initialize file utilities
            TableFileUtils.initialize(getServletContext());
            ReservationFileUtils.initialize(getServletContext());

            // Initialize services
            tableService = TableService.getInstance();
            reservationService = ReservationService.getInstance();

            // Store services in servlet context
            getServletContext().setAttribute("tableService", tableService);
            getServletContext().setAttribute("reservationService", reservationService);

            System.out.println("UserTablesServlet initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing UserTablesServlet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Failed to initialize UserTablesServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("User not logged in - redirecting to login");
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            System.out.println("UserTablesServlet: Loading tables for user");

            // Get all tables and convert to List for easier handling
            Table[] tablesArray = tableService.getAllTables();
            List<Table> tables = new ArrayList<>();

            if (tablesArray != null) {
                for (Table table : tablesArray) {
                    if (table != null) {
                        tables.add(table);
                    }
                }
            }

            // Set tables attribute
            request.setAttribute("tables", tables);

            // Add some debug info
            System.out.println("UserTablesServlet: Loaded " + tables.size() + " tables");
            for (Table table : tables) {
                System.out.println("  Table " + table.getNumber() + " - Available: " + table.isAvailable());
            }

            // Forward to JSP
            request.getRequestDispatcher("userTables.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in UserTablesServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading tables: " + e.getMessage());
            request.getRequestDispatcher("userTables.jsp").forward(request, response);
        }
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
        System.out.println("UserTablesServlet: Processing action: " + action);

        try {
            if ("reserve".equals(action)) {
                handleReservation(request, response, session);
            } else {
                System.out.println("Unknown action: " + action);
                response.sendRedirect("userTables");
            }
        } catch (Exception e) {
            System.err.println("Error in UserTablesServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void handleReservation(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
            String tableIdStr = request.getParameter("tableId");
            String reservationTimeStr = request.getParameter("reservationTime");

            System.out.println("UserTablesServlet: Handling reservation - tableId: " + tableIdStr + ", time: " + reservationTimeStr);

            if (tableIdStr == null || reservationTimeStr == null) {
                request.setAttribute("error", "Missing reservation parameters");
                doGet(request, response);
                return;
            }

            int tableId = Integer.parseInt(tableIdStr);
            int userId = (int) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");

            System.out.println("UserTablesServlet: Reservation for userId: " + userId + ", username: " + username);

            // Get the table
            Table table = tableService.getTableById(tableId);
            if (table == null) {
                System.out.println("UserTablesServlet: Table not found");
                request.setAttribute("error", "Table not found");
                doGet(request, response);
                return;
            }

            if (!table.isAvailable()) {
                System.out.println("UserTablesServlet: Table is not available");
                request.setAttribute("error", "Table is no longer available");
                doGet(request, response);
                return;
            }

            // Create reservation
            Reservation newReservation = new Reservation();
            newReservation.setUserId(userId);
            newReservation.setTableId(tableId);
            newReservation.setReservationTime(reservationTimeStr);
            newReservation.setCustomerName(username != null ? username : "Guest");
            newReservation.setStatus("confirmed");

            System.out.println("UserTablesServlet: Creating reservation: " + newReservation);

            // Add reservation (this will trigger merge sort)
            reservationService.addReservation(newReservation);
            System.out.println("Reservation created successfully: " + newReservation);

            // Update table availability
            tableService.updateTable(
                    table.getId(),
                    table.getNumber(),
                    table.getCapacity(),
                    false, // Set to not available
                    table.getCategory()
            );

            request.setAttribute("message", "Table " + table.getNumber() + " reserved successfully!");
            System.out.println("Table " + table.getNumber() + " marked as unavailable");

        } catch (NumberFormatException e) {
            System.err.println("Invalid table ID: " + e.getMessage());
            request.setAttribute("error", "Invalid table ID");
        } catch (Exception e) {
            System.err.println("Error creating reservation: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during reservation: " + e.getMessage());
        }

        // Redirect back to tables view
        response.sendRedirect("userTables");
    }
}