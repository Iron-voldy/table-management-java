package com.example.restaurant_table_reservation.service;

import com.example.restaurant_table_reservation.model.Order;
import com.example.restaurant_table_reservation.utils.OrderFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private List<Order> orderList;
    private Gson gson = new Gson();

    public OrderService() {
        loadOrders();
    }

    private void loadOrders() {
        try {
            String json = OrderFileUtils.readFile();
            Type listType = new TypeToken<List<Order>>(){}.getType();
            orderList = gson.fromJson(json, listType);
            if (orderList == null) orderList = new ArrayList<>();
            System.out.println("Loaded Orders: " + orderList);
        } catch (Exception e) {
            System.err.println("Failed to load orders: " + e.getMessage());
            orderList = new ArrayList<>();
        }
    }

    private void saveOrders() {
        try {
            OrderFileUtils.writeFile(gson.toJson(orderList));
        } catch (Exception e) {
            System.err.println("Failed to save orders: " + e.getMessage());
        }
    }

    public List<Order> getAllOrders() {
        loadOrders(); // Reload orders from file to get latest data
        return orderList;
    }

    public void addOrder(Order order) {
        int id = orderList.isEmpty() ? 1 : orderList.get(orderList.size() - 1).getId() + 1;
        order.setId(id);
        orderList.add(order);
        saveOrders();
    }

    public void deleteOrder(int id) {
        orderList.removeIf(order -> order.getId() == id);
        saveOrders();
    }

    public void updateOrder(Order updatedOrder) {
        for (Order order : orderList) {
            if (order.getId() == updatedOrder.getId()) {
                order.setCustomerName(updatedOrder.getCustomerName());
                order.setTableNumber(updatedOrder.getTableNumber());
                order.setOrderDetails(updatedOrder.getOrderDetails());
                order.setTotalPrice(updatedOrder.getTotalPrice());
                saveOrders();
                break;
            }
        }
    }

    public Order getOrderById(int id) {
        for (Order order : orderList) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }
}