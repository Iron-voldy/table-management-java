package com.example.restaurant_table_reservation.servlet;

import com.example.restaurant_table_reservation.model.Table;
import com.example.restaurant_table_reservation.service.TableService;
import com.example.restaurant_table_reservation.utils.TableFileUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/tables")
public class TableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TableService service;

    @Override
    public void init() throws ServletException {
        service = TableService.getInstance();
        TableFileUtils.initialize(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle GET request (e.g., display existing tables)
        // TODO: Implement fetching and setting tables attribute if needed
        request.getRequestDispatcher("/tables.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract parameters from the request
        int id = Integer.parseInt(request.getParameter("id"));
        int number = Integer.parseInt(request.getParameter("number"));
        int capacity = Integer.parseInt(request.getParameter("capacity"));
        boolean available = Boolean.parseBoolean(request.getParameter("available"));
        String category = request.getParameter("category");

        // Add the new table
        service.addTable(id, number, capacity, available, category);

        // Redirect back to the tables page or dashboard
        response.sendRedirect("tables");
    }
}