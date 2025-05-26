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
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        h2 {
            color: #333;
            font-size: 2rem;
        }
        .nav-links {
            text-align: center;
            margin-bottom: 20px;
        }
        .nav-links a {
            display: inline-block;
            margin: 0 10px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        .nav-links a:hover {
            background-color: #0056b3;
        }
        .message {
            text-align: center;
            margin: 20px 0;
            padding: 15px;
            border-radius: 5px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        table {
            width: 90%;
            margin: 20px auto;
            border-collapse: collapse;
            background-color: #fff;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            border-radius: 10px;
            overflow: hidden;
        }
        th, td {
            padding: 15px 12px;
            text-align: center;
            color: #000000;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #4CAF50;
            color: white;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .reserve-btn {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 10px 20px;
            font-size: 14px;
            border-radius: 5px;
            cursor: pointer;
            transition: all 0.3s;
        }
        .reserve-btn:hover {
            background-color: #218838;
            transform: translateY(-2px);
        }
        .reservation-form {
            display: inline-block;
            margin: 0 10px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: 500;
        }
        .form-group input[type="datetime-local"] {
            padding: 8px 12px;
            border-radius: 4px;
            border: 1px solid #ccc;
            font-size: 14px;
            width: 180px;
        }
        .table-number {
            font-weight: bold;
            color: #007bff;
            font-size: 1.2em;
        }
        .table-capacity {
            color: #28a745;
            font-weight: 600;
        }
        .table-category {
            padding: 4px 8px;
            background-color: #e9ecef;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
            text-transform: uppercase;
        }
        .available {
            color: #28a745;
            font-weight: bold;
        }
        .not-available {
            color: #dc3545;
            font-weight: bold;
        }
        .queue-section {
            text-align: center;
            margin-top: 40px;
            padding: 30px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .queue-section h3 {
            color: #007bff;
            margin-bottom: 15px;
        }
        .queue-btn {
            display: inline-block;
            margin: 0 10px;
            padding: 15px 30px;
            background-color: #ffc107;
            color: #212529;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s;
        }
        .queue-btn:hover {
            background-color: #e0a800;
            transform: translateY(-2px);
        }
        .features-info {
            background-color: #e3f2fd;
            border-left: 4px solid #2196f3;
            padding: 20px;
            margin: 20px auto;
            width: 90%;
            border-radius: 5px;
        }
        .features-info h4 {
            color: #1976d2;
            margin-bottom: 10px;
        }
        .features-info ul {
            margin: 0;
            padding-left: 20px;
        }
        .features-info li {
            margin: 5px 0;
            color: #424242;
        }
        .no-tables {
            text-align: center;
            padding: 50px;
            color: #666;
            font-size: 1.2em;
        }
    </style>
</head>
<body>
    <div class="header">
        <h2>Available Tables for Reservation</h2>
        <p>Select a table and choose your preferred date and time</p>
    </div>

    <div class="nav-links">
        <a href="index.jsp">🏠 Home</a>
        <a href="categoryMenu">🍽️ View Menu</a>
        <a href="userReviews">⭐ Reviews</a>
        <a href="logout">🚪 Logout</a>
    </div>

    <!-- Display Messages -->
    <% if (request.getAttribute("error") != null) { %>
        <div class="message error">
            <strong>Error:</strong> <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <% if (request.getAttribute("message") != null) { %>
        <div class="message success">
            <strong>Success:</strong> <%= request.getAttribute("message") %>
        </div>
    <% } %>

    <!-- Features Information -->
    <div class="features-info">
        <h4>🔧 System Features</h4>
        <ul>
            <li><strong>Custom Queue:</strong> Reservations are managed using a custom queue data structure</li>
            <li><strong>Merge Sort:</strong> All reservations are automatically sorted by time using merge sort algorithm</li>
            <li><strong>Real-time Updates:</strong> Table availability is updated immediately after booking</li>
            <li><strong>Time Validation:</strong> System prevents booking tables for past dates</li>
        </ul>
    </div>

    <!-- Tables Display -->
    <table>
        <thead>
            <tr>
                <th>Table Number</th>
                <th>Capacity</th>
                <th>Category</th>
                <th>Availability</th>
                <th>Reserve</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Table> tables = (List<Table>) request.getAttribute("tables");
                if (tables != null && !tables.isEmpty()) {
                    boolean hasAvailableTables = false;
                    for (Table table : tables) {
                        if (table.isAvailable()) {
                            hasAvailableTables = true;
            %>
            <tr>
                <td>
                    <span class="table-number">Table <%= table.getNumber() %></span>
                </td>
                <td>
                    <span class="table-capacity"><%= table.getCapacity() %> guests</span>
                </td>
                <td>
                    <span class="table-category"><%= table.getCategory() != null ? table.getCategory() : "Standard" %></span>
                </td>
                <td>
                    <span class="available">✅ Available</span>
                </td>
                <td>
                    <form action="reserve" method="POST" class="reservation-form">
                        <input type="hidden" name="tableId" value="<%= table.getId() %>">
                        <div class="form-group">
                            <label for="reservationTime-<%= table.getId() %>">📅 Date & Time:</label>
                            <input type="datetime-local"
                                   id="reservationTime-<%= table.getId() %>"
                                   name="reservationTime"
                                   required>
                        </div>
                        <button type="submit" class="reserve-btn">🍽️ Reserve Table</button>
                    </form>
                </td>
            </tr>
            <%
                        }
                    }

                    // Show unavailable tables
                    for (Table table : tables) {
                        if (!table.isAvailable()) {
            %>
            <tr style="opacity: 0.6; background-color: #f8f9fa;">
                <td>
                    <span class="table-number">Table <%= table.getNumber() %></span>
                </td>
                <td>
                    <span class="table-capacity"><%= table.getCapacity() %> guests</span>
                </td>
                <td>
                    <span class="table-category"><%= table.getCategory() != null ? table.getCategory() : "Standard" %></span>
                </td>
                <td>
                    <span class="not-available">❌ Reserved</span>
                </td>
                <td>
                    <em>Not Available</em>
                </td>
            </tr>
            <%
                        }
                    }

                    if (!hasAvailableTables) {
            %>
            <tr>
                <td colspan="5" class="no-tables">
                    <strong>No tables are currently available.</strong><br>
                    Please check the reservation queue or try again later.
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="5" class="no-tables">
                    <strong>No tables found.</strong><br>
                    Please contact the administrator.
                </td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>

    <!-- Queue Management Section -->
    <div class="queue-section">
        <h3>🕒 Reservation Queue Management</h3>
        <p>Manage your reservations with our advanced queue system powered by custom data structures</p>
        <a href="reservationQueue" class="queue-btn">
            📋 View Reservation Queue
        </a>
        <a href="adminReservations" class="queue-btn" style="background-color: #17a2b8; color: white;">
            👨‍💼 View All Reservations
        </a>
        <p style="margin-top: 15px; color: #666; font-size: 14px;">
            <strong>Technical Features:</strong> Custom Queue Implementation • Merge Sort Algorithm • Real-time Updates
        </p>
    </div>

    <script>
        // Set minimum datetime to current time for all datetime inputs
        document.addEventListener('DOMContentLoaded', function() {
            const now = new Date();
            const offset = now.getTimezoneOffset() * 60000; // offset in milliseconds
            const localISOTime = (new Date(now - offset)).toISOString().slice(0, 16);

            const dateTimeInputs = document.querySelectorAll('input[type="datetime-local"]');
            dateTimeInputs.forEach(input => {
                input.min = localISOTime;
            });
        });

        // Form validation
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('submit', function(e) {
                const dateTimeInput = this.querySelector('input[type="datetime-local"]');
                const selectedTime = new Date(dateTimeInput.value);
                const now = new Date();

                if (selectedTime <= now) {
                    e.preventDefault();
                    alert('Please select a future date and time for your reservation.');
                    return false;
                }

                // Confirm reservation
                const tableNumber = this.closest('tr').querySelector('.table-number').textContent;
                const formattedTime = selectedTime.toLocaleString();

                if (!confirm(`Confirm reservation for ${tableNumber} on ${formattedTime}?`)) {
                    e.preventDefault();
                    return false;
                }
            });
        });

        // Auto-hide messages after 5 seconds
        setTimeout(function() {
            const messages = document.querySelectorAll('.message');
            messages.forEach(message => {
                message.style.opacity = '0';
                setTimeout(() => message.remove(), 300);
            });
        }, 5000);
    </script>
</body>
</html>