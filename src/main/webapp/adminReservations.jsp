<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.example.restaurant_table_reservation.model.Reservation, com.example.restaurant_table_reservation.model.User, com.example.restaurant_table_reservation.model.Table, com.example.restaurant_table_reservation.service.UserService, com.example.restaurant_table_reservation.service.TableService" %>
<html>
<head>
    <title>Admin Reservations</title>
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
         button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px 16px;
            font-size: 14px;
            border-radius: 5px;
            cursor: pointer;
            margin-bottom: 20px;
        }
        button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
<button onclick="history.back()">← Back to Admin Dashboard</button>
<h2>All Reservations</h2>

<table>
    <thead>
        <tr>
            <th>Reservation ID</th>
            <th>User</th>
            <th>Table Number</th>
            <th>Reservation Time</th>
            <!-- Add more headers if needed (e.g., actions like cancel) -->
        </tr>
    </thead>
    <tbody>
        <% 
            List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
            UserService userService = (UserService) request.getAttribute("userService");
            TableService tableService = (TableService) request.getAttribute("tableService");
            
            if (reservations != null && !reservations.isEmpty()) {
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
        %>
        <tr>
            <td><%= reservation.getId() %></td>
            <td><%= reservedByUser != null ? reservedByUser.getUsername() : "Unknown User" %></td>
            <td><%= reservedTable != null ? reservedTable.getNumber() : "Unknown Table" %></td>
            <td><%= reservation.getReservationTime() %></td>
            <!-- Add more cells for actions if needed -->
        </tr>
        <% 
                }
            } else {
        %>
        <tr>
            <td colspan="4">No reservations found.</td>
        </tr>
        <% 
            }
        %>
    </tbody>
</table>

</body>
</html> 