package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;

import com.example.restaurant_table_reservation.model.Order;
import com.example.restaurant_table_reservation.service.OrderService;
import com.example.restaurant_table_reservation.utils.OrderFileUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AddOrderServlet extends HttpServlet {
    private OrderService service;

    @Override
    public void init() throws ServletException {
        OrderFileUtils.initialize(getServletContext()); // Initialize file path
        service = new OrderService();
        getServletContext().setAttribute("orderService", service);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Retrieve and validate parameters
        String customerName = req.getParameter("customerName");
        String tableNumberStr = req.getParameter("tableNumber");
        String orderDetails = req.getParameter("orderDetails");
        String totalPriceStr = req.getParameter("totalPrice");

        // Check for null or empty values
        if (customerName == null || customerName.trim().isEmpty() ||
                tableNumberStr == null || tableNumberStr.trim().isEmpty() ||
                orderDetails == null || orderDetails.trim().isEmpty() ||
                totalPriceStr == null || totalPriceStr.trim().isEmpty()) {
            resp.sendRedirect("userAddOrder.jsp?error=missingFields");
            return;
        }

        try {
            int tableNumber = Integer.parseInt(tableNumberStr.trim());
            double totalPrice = Double.parseDouble(totalPriceStr.trim());

            // Create and save the order
            service.addOrder(new Order(customerName.trim(), tableNumber, orderDetails.trim(), totalPrice));

            resp.sendRedirect("index.jsp");
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format for tableNumber or totalPrice: " + e.getMessage());
            resp.sendRedirect("userAddOrder.jsp?error=invalidNumberFormat");
        } catch (Exception e) {
            System.err.println("Unexpected error in AddOrderServlet: " + e.getMessage());
            resp.sendRedirect("userAddOrder.jsp?error=unexpectedError");
        }
    }
}