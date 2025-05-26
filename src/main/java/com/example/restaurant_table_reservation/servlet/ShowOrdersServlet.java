package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;
import java.util.List;

import com.example.restaurant_table_reservation.model.Order;
import com.example.restaurant_table_reservation.service.OrderService;
import com.example.restaurant_table_reservation.utils.OrderFileUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ShowOrdersServlet extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        OrderFileUtils.initialize(getServletContext()); // Initialize file path
        orderService = new OrderService();
        getServletContext().setAttribute("orderService", orderService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Order> orders = orderService.getAllOrders();
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("showOrders.jsp").forward(req, resp);
    }
}