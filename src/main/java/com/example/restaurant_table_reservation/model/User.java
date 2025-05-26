package com.example.restaurant_table_reservation.model;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String address;
    private boolean isAdmin;
    private String firstName;
    private String lastName;

    // Existing no-arg constructor
    public User() {
    }

    // Constructor for id, username, email (for AdminServlet usage)
    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = null; // Default to null
        this.phone = null;    // Default to null
        this.address = null;  // Default to null
        this.isAdmin = false; // Default to false
        this.firstName = null; // Default to null
        this.lastName = null;  // Default to null
    }

    // Existing constructor with 6 parameters
    public User(int id, String username, String email, String password, String phone, String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.isAdmin = false; // Default to false
        this.firstName = null; // Default to null
        this.lastName = null;  // Default to null
    }

    // Existing constructor with 7 parameters
    public User(int id, String username, String email, String password, String phone, String address, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.isAdmin = isAdmin;
        this.firstName = null; // Default to null
        this.lastName = null;  // Default to null
    }

    // New constructor with all fields
    public User(int id, String username, String email, String password, String phone, String address, boolean isAdmin, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}