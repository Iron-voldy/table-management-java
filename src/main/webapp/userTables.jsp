<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.example.restaurant_table_reservation.model.Table" %>
<html>
<head>
    <title>Available Tables</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f9f9f9;
        }
        h2 {
            text-align: center;
            color: #333;
        }
        table {
            width: 80%;
            margin: 20px auto;
            border-collapse: collapse;
            background-color: #fff;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: center;
            color: #000000;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        .reserve-btn {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px 16px;
            font-size: 14px;
            border-radius: 5px;
            cursor: pointer;
        }
        .reserve-btn:hover {
            background-color: #0056b3;
        }
        .reservation-form {
            display: inline-block;
            margin: 0 10px;
        }
        .form-group {
            margin-bottom: 10px;
        }
        .form-group label {
            display: inline-block;
            width: 100px;
            color: #333;
        }
        .form-group input[type="datetime-local"] {
            padding: 8px;
            border-radius: 4px;
            border: 1px solid #ccc;
            font-size: 14px;
        }
        .error {
            color: #dc3545;
            font-size: 14px;
            text-align: center;
            margin: 10px 0;
        }
    </style>
</head>
<body>
<h2>Available Tables</h2>
<% if (request.getAttribute("error") != null) { %>
<div class="error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (request.getAttribute("message") != null) { %>
<div class="error" style="color: #28a745;"><%= request.getAttribute("message") %></div>
<% } %>
<table>
    <thead>
    <tr>
        <th>Table Number</th>
        <th>Capacity</th>
        <th>Availability</th>
        <th>Category</th>
        <th>Reserve</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Table> tables = (List<Table>) request.getAttribute("tables");
        if (tables != null && !tables.isEmpty()) {
            for (Table table : tables) {
                if (table.isAvailable()) { // Only show available tables
    %>
    <tr>
        <td><%= table.getNumber() %></td>
        <td><%= table.getCapacity() %></td>
        <td><%= table.isAvailable() ? "Available" : "Not Available" %></td>
        <td><%= table.getCategory() != null ? table.getCategory() : "" %></td>
        <td>
            <form action="reserve" method="POST" class="reservation-form">
                <input type="hidden" name="action" value="reserve">
                <input type="hidden" name="tableId" value="<%= table.getId() %>">
                <div class="form-group">
                    <label for="reservationTime-<%= table.getId() %>">Date & Time:</label>
                    <input type="datetime-local" id="reservationTime-<%= table.getId() %>" name="reservationTime" required>
                </div>
                <button type="submit" class="reserve-btn">Reserve</button>
            </form>
        </td>
    </tr>
    <%
            }
        }
    } else {
    %>
    <tr>
        <td colspan="5">No tables available.</td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>

<!-- Add this after the existing table content -->
<div style="text-align: center; margin-top: 30px;">
    <a href="reservationQueue" class="btn btn-warning" style="padding: 12px 24px; font-size: 16px;">
        <i class="fas fa-clock"></i> View Reservation Queue
    </a>
    <p style="margin-top: 10px; color: #666; font-size: 14px;">
        Check queue status and manage your reservations
    </p>
</div>
</body>
</html>