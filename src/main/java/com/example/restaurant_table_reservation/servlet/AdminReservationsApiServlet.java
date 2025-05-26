package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.Reservation;
import com.example.restaurant_table_reservation.model.User;
import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.service.ReservationService;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.service.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/admin/reservations")
public class AdminReservationsApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservationService reservationService;
    private UserService userService;
    private TableService tableService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        reservationService = ReservationService.getInstance();
        userService = UserService.getInstance();
        tableService = TableService.getInstance();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Basic check if user is logged in and is admin (can be enhanced)
         if (session == null || session.getAttribute("isAdmin") == null || !(Boolean) session.getAttribute("isAdmin")) {
             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
             return;
         }

        List<Reservation> reservations = reservationService.getAllReservations();

        // For the API, we might want to return richer data than just IDs,
        // including username and table number directly.
        List<ReservationDetails> reservationDetailsList = new ArrayList<>();
        if (reservations != null) {
            for (Reservation reservation : reservations) {
                 User reservedByUser = null;
                 if (userService != null) {
                      List<User> allUsers = userService.getAllUsers();
                      for(User user : allUsers) {
                          if (user.getId() == reservation.getUserId()) {
                              reservedByUser = user;
                              break;
                          }
                      }
                 }
                 
                 Table reservedTable = null;
                  if (tableService != null) {
                      Table[] allTables = tableService.getAllTables();
                      for(Table table : allTables) {
                          if (table.getId() == reservation.getTableId()) {
                              reservedTable = table;
                              break;
                          }
                      }
                 }

                reservationDetailsList.add(new ReservationDetails(
                    reservation.getId(),
                    reservedByUser != null ? reservedByUser.getUsername() : "Unknown User",
                    reservedTable != null ? reservedTable.getNumber() : -1,
                    reservation.getReservationTime()
                ));
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(reservationDetailsList));
    }

    // Helper class to structure the JSON response
    private static class ReservationDetails {
        int id;
        String username;
        int tableNumber;
        String reservationTime;

        public ReservationDetails(int id, String username, int tableNumber, String reservationTime) {
            this.id = id;
            this.username = username;
            this.tableNumber = tableNumber;
            this.reservationTime = reservationTime;
        }
    }
} 