package com.example.restaurant_table_reservation.model;

public class Table {
    private int id;
    private int number;
    private int capacity;
    private boolean available;
    private String category;

    public Table(int id, int number, int capacity, boolean available, String category) {
        this.id = id;
        this.number = number;
        this.capacity = capacity;
        this.available = available;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getCategory() {
        return category;
    }
}