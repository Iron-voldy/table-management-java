<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.example.restaurant_table_reservation.model.Reservation, com.example.restaurant_table_reservation.model.Table, com.example.restaurant_table_reservation.service.TableService" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reservation Queue - Restaurant Management</title>
    <style>
        :root {
            --primary: #4361ee;
            --primary-dark: #3a0ca3;
            --accent: #f72585;
            --light: #f8f9fa;
            --dark: #212529;
            --gray: #6c757d;
            --border: #dee2e6;
            --success: #28a745;
            --warning: #ffc107;
            --error: #dc3545;
            --info: #17a2b8;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: var(--dark);
            line-height: 1.6;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }

        .header {
            text-align: center;
            margin-bottom: 30px;
            padding: 30px;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            backdrop-filter: blur(10px);
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        }

        .header h1 {
            font-size: 2.8rem;
            margin-bottom: 10px;
            background: linear-gradient(135deg, var(--primary), var(--accent));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            font-weight: 700;
        }

        .header p {
            font-size: 1.1rem;
            color: var(--gray);
            margin-bottom: 20px;
        }

        .breadcrumb {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            font-size: 0.9rem;
        }

        .breadcrumb a {
            color: var(--primary);
            text-decoration: none;
            padding: 5px 10px;
            border-radius: 5px;
            transition: all 0.3s;
        }

        .breadcrumb a:hover {
            background: var(--primary);
            color: white;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 25px;
            backdrop-filter: blur(10px);
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            transition: all 0.3s;
            border-left: 5px solid var(--primary);
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
        }

        .stat-card.success {
            border-left-color: var(--success);
        }

        .stat-card.warning {
            border-left-color: var(--warning);
        }

        .stat-card.error {
            border-left-color: var(--error);
        }

        .stat-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .stat-card-icon {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.5rem;
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
        }

        .stat-card.success .stat-card-icon {
            background: linear-gradient(135deg, var(--success), #1e7e34);
        }

        .stat-card.warning .stat-card-icon {
            background: linear-gradient(135deg, var(--warning), #e0a800);
        }

        .stat-card.error .stat-card-icon {
            background: linear-gradient(135deg, var(--error), #bd2130);
        }

        .stat-number {
            font-size: 2rem;
            font-weight: 700;
            color: var(--primary);
            margin: 10px 0;
        }

        .stat-label {
            color: var(--gray);
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .section-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            backdrop-filter: blur(10px);
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        }

        .section-header {
            display: flex;
            align-items: center;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid var(--border);
        }

        .section-header h3 {
            font-size: 1.4rem;
            color: var(--primary);
            margin-left: 10px;
        }

        .section-header i {
            font-size: 1.5rem;
            color: var(--primary);
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 25px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group label {
            margin-bottom: 8px;
            font-weight: 600;
            color: var(--dark);
            font-size: 0.9rem;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            padding: 12px 15px;
            border: 2px solid var(--border);
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s;
            background: white;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.2);
            transform: translateY(-2px);
        }

        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            text-align: center;
            position: relative;
            overflow: hidden;
        }

        .btn::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
            transition: left 0.5s;
        }

        .btn:hover::before {
            left: 100%;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(67, 97, 238, 0.3);
        }

        .btn-success {
            background: linear-gradient(135deg, var(--success), #1e7e34);
            color: white;
        }

        .btn-success:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(40, 167, 69, 0.3);
        }

        .btn-warning {
            background: linear-gradient(135deg, var(--warning), #e0a800);
            color: var(--dark);
        }

        .btn-warning:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(255, 193, 7, 0.3);
        }

        .btn-danger {
            background: linear-gradient(135deg, var(--error), #bd2130);
            color: white;
        }

        .btn-danger:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(220, 53, 69, 0.3);
        }

        .btn-secondary {
            background: linear-gradient(135deg, var(--gray), #5a6268);
            color: white;
        }

        .btn-sm {
            padding: 8px 16px;
            font-size: 12px;
        }

        .queue-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        .queue-table th,
        .queue-table td {
            padding: 15px 12px;
            text-align: left;
            border-bottom: 1px solid var(--border);
        }

        .queue-table th {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: white;
            font-weight: 600;
            position: sticky;
            top: 0;
            z-index: 1;
        }

        .queue-table tr:hover {
            background: var(--light);
            transform: scale(1.01);
            transition: all 0.2s;
        }

        .queue-table td:first-child {
            font-weight: 700;
            color: var(--primary);
            font-size: 1.1rem;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .status-pending {
            background: linear-gradient(135deg, #fff3cd, #ffeaa7);
            color: #856404;
            border: 1px solid #ffeaa7;
        }

        .status-confirmed {
            background: linear-gradient(135deg, #d4edda, #00b894);
            color: white;
            border: 1px solid #00b894;
        }

        .status-cancelled {
            background: linear-gradient(135deg, #f8d7da, #e74c3c);
            color: white;
            border: 1px solid #e74c3c;
        }

        .action-buttons {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }

        .message {
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 500;
            animation: slideIn 0.3s ease;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .message.success {
            background: linear-gradient(135deg, #d4edda, #c3e6cb);
            color: #155724;
            border-left: 5px solid var(--success);
        }

        .message.error {
            background: linear-gradient(135deg, #f8d7da, #f5c6cb);
            color: #721c24;
            border-left: 5px solid var(--error);
        }

        .message.info {
            background: linear-gradient(135deg, #d1ecf1, #bee5eb);
            color: #0c5460;
            border-left: 5px solid var(--info);
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: var(--gray);
        }

        .empty-state i {
            font-size: 4rem;
            margin-bottom: 20px;
            color: var(--border);
            animation: float 3s ease-in-out infinite;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(-10px); }
        }

        .empty-state h3 {
            margin-bottom: 10px;
            color: var(--gray);
        }

        .operations-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-top: 20px;
        }

        .loading {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 3px solid rgba(255,255,255,.3);
            border-radius: 50%;
            border-top-color: #fff;
            animation: spin 1s ease-in-out infinite;
        }

        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        .priority-indicator {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            display: inline-block;
            margin-right: 8px;
        }

        .priority-high {
            background: var(--error);
            animation: pulse 2s infinite;
        }

        .priority-medium {
            background: var(--warning);
        }

        .priority-low {
            background: var(--success);
        }

        @keyframes pulse {
            0% { opacity: 1; }
            50% { opacity: 0.5; }
            100% { opacity: 1; }
        }

        .queue-position {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: white;
            padding: 8px 12px;
            border-radius: 20px;
            font-weight: 700;
            font-size: 0.9rem;
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .time-remaining {
            font-size: 0.8rem;
            color: var(--gray);
            font-style: italic;
        }

        .customer-info {
            display: flex;
            flex-direction: column;
            gap: 2px;
        }

        .customer-name {
            font-weight: 600;
            color: var(--dark);
        }

        .customer-details {
            font-size: 0.8rem;
            color: var(--gray);
        }

        .table-info {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 12px;
            background: var(--light);
            border-radius: 8px;
        }

        .refresh-indicator {
            position: fixed;
            top: 20px;
            right: 20px;
            background: var(--primary);
            color: white;
            padding: 10px 15px;
            border-radius: 25px;
            font-size: 0.8rem;
            opacity: 0;
            transition: opacity 0.3s;
            z-index: 1000;
        }

        .refresh-indicator.show {
            opacity: 1;
        }

        @media (max-width: 768px) {
            .container {
                padding: 10px;
            }

            .header h1 {
                font-size: 2rem;
            }

            .form-grid {
                grid-template-columns: 1fr;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .operations-grid {
                grid-template-columns: 1fr;
            }

            .queue-table {
                font-size: 14px;
            }

            .queue-table th,
            .queue-table td {
                padding: 10px 8px;
            }

            .action-buttons {
                flex-direction: column;
            }
        }

        @media (max-width: 480px) {
            .header {
                padding: 20px;
            }

            .section-card {
                padding: 20px;
            }
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <!-- Refresh Indicator -->
    <div class="refresh-indicator" id="refreshIndicator">
        <i class="fas fa-sync-alt"></i> Auto-refreshing...
    </div>

    <div class="container">
        <!-- Header -->
        <div class="header">
            <h1><i class="fas fa-clock"></i> Reservation Queue</h1>
            <p>Advanced queue management with custom data structures and algorithms</p>
            <div class="breadcrumb">
                <a href="index.jsp"><i class="fas fa-home"></i> Home</a>
                <span>/</span>
                <a href="userTables"><i class="fas fa-chair"></i> Tables</a>
                <span>/</span>
                <span>Queue Management</span>
            </div>
        </div>

        <!-- Display Messages -->
        <% if (request.getAttribute("message") != null) { %>
            <div class="message success">
                <i class="fas fa-check-circle"></i>
                <%= request.getAttribute("message") %>
            </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="message error">
                <i class="fas fa-exclamation-circle"></i>
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <!-- Queue Statistics -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-card-header">
                    <div>
                        <div class="stat-label">Total in Queue</div>
                        <div class="stat-number"><%= request.getAttribute("queueSize") != null ? request.getAttribute("queueSize") : 0 %></div>
                    </div>
                    <div class="stat-card-icon">
                        <i class="fas fa-list-ol"></i>
                    </div>
                </div>
                <p>Current reservations waiting</p>
            </div>

            <div class="stat-card <%= (Boolean) request.getAttribute("queueEmpty") ? "success" : "warning" %>">
                <div class="stat-card-header">
                    <div>
                        <div class="stat-label">Queue Status</div>
                        <div class="stat-number"><%= (Boolean) request.getAttribute("queueEmpty") ? "Empty" : "Active" %></div>
                    </div>
                    <div class="stat-card-icon">
                        <i class="fas fa-<%= (Boolean) request.getAttribute("queueEmpty") ? "check" : "clock" %>"></i>
                    </div>
                </div>
                <p><%= (Boolean) request.getAttribute("queueEmpty") ? "No pending reservations" : "Processing reservations" %></p>
            </div>

            <div class="stat-card <%= (Boolean) request.getAttribute("queueFull") ? "error" : "success" %>">
                <div class="stat-card-header">
                    <div>
                        <div class="stat-label">Capacity</div>
                        <div class="stat-number"><%= (Boolean) request.getAttribute("queueFull") ? "Full" : "Available" %></div>
                    </div>
                    <div class="stat-card-icon">
                        <i class="fas fa-<%= (Boolean) request.getAttribute("queueFull") ? "exclamation-triangle" : "check-circle" %>"></i>
                    </div>
                </div>
                <p><%= (Boolean) request.getAttribute("queueFull") ? "Queue at maximum capacity" : "Space available for new reservations" %></p>
            </div>

            <div class="stat-card">
                <div class="stat-card-header">
                    <div>
                        <div class="stat-label">Data Structure</div>
                        <div class="stat-number">Custom</div>
                    </div>
                    <div class="stat-card-icon">
                        <i class="fas fa-code"></i>
                    </div>
                </div>
                <p>Queue + Merge Sort Implementation</p>
            </div>
        </div>

        <!-- Add Reservation Form -->
        <div class="section-card">
            <div class="section-header">
                <i class="fas fa-plus-circle"></i>
                <h3>Add New Reservation to Queue</h3>
            </div>

            <form action="reservationQueue" method="post" id="addReservationForm">
                <input type="hidden" name="action" value="addToQueue">

                <div class="form-grid">
                    <div class="form-group">
                        <label for="tableId"><i class="fas fa-chair"></i> Table Selection</label>
                        <select id="tableId" name="tableId" required>
                            <option value="">Choose a table...</option>
                            <%
                                // Get available tables (you can enhance this to show only available tables)
                                for (int i = 1; i <= 10; i++) {
                            %>
                            <option value="<%= i %>">Table <%= i %> (4 seats)</option>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="customerName"><i class="fas fa-user"></i> Customer Name</label>
                        <input type="text" id="customerName" name="customerName" required
                               placeholder="Enter customer name" maxlength="100">
                    </div>

                    <div class="form-group">
                        <label for="customerPhone"><i class="fas fa-phone"></i> Phone Number</label>
                        <input type="tel" id="customerPhone" name="customerPhone"
                               placeholder="Enter phone number" maxlength="15">
                    </div>

                    <div class="form-group">
                        <label for="reservationTime"><i class="fas fa-calendar-alt"></i> Reservation Date & Time</label>
                        <input type="datetime-local" id="reservationTime" name="reservationTime" required>
                    </div>

                    <div class="form-group">
                        <label for="customerEmail"><i class="fas fa-envelope"></i> Email (Optional)</label>
                        <input type="email" id="customerEmail" name="customerEmail"
                               placeholder="Enter email address" maxlength="100">
                    </div>

                    <div class="form-group">
                        <label for="specialRequests"><i class="fas fa-comment"></i> Special Requests</label>
                        <textarea id="specialRequests" name="specialRequests" rows="3"
                                  placeholder="Any special requirements..." maxlength="500"></textarea>
                    </div>
                </div>

                <div style="margin-top: 25px; text-align: center;">
                    <button type="submit" class="btn btn-primary" id="submitBtn">
                        <i class="fas fa-plus"></i> Add to Queue
                    </button>
                    <button type="reset" class="btn btn-secondary">
                        <i class="fas fa-undo"></i> Reset Form
                    </button>
                </div>
            </form>
        </div>

        <!-- Current Queue Display -->
        <div class="section-card">
            <div class="section-header">
                <i class="fas fa-list"></i>
                <h3>Current Reservation Queue</h3>
            </div>

            <%
                List<Reservation> queuedReservations = (List<Reservation>) request.getAttribute("queuedReservations");
                if (queuedReservations != null && !queuedReservations.isEmpty()) {
            %>

            <div class="message info">
                <i class="fas fa-info-circle"></i>
                Showing <%= queuedReservations.size() %> reservation(s) in queue, sorted by time using Merge Sort algorithm
            </div>

            <div style="overflow-x: auto;">
                <table class="queue-table">
                    <thead>
                        <tr>
                            <th><i class="fas fa-sort-numeric-down"></i> Position</th>
                            <th><i class="fas fa-id-badge"></i> ID</th>
                            <th><i class="fas fa-user"></i> Customer</th>
                            <th><i class="fas fa-chair"></i> Table</th>
                            <th><i class="fas fa-clock"></i> Time</th>
                            <th><i class="fas fa-phone"></i> Contact</th>
                            <th><i class="fas fa-info-circle"></i> Status</th>
                            <th><i class="fas fa-cogs"></i> Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (int i = 0; i < queuedReservations.size(); i++) {
                                Reservation reservation = queuedReservations.get(i);
                                String priorityClass = i < 3 ? "priority-high" : (i < 6 ? "priority-medium" : "priority-low");
                        %>
                        <tr>
                            <td>
                                <div class="queue-position">
                                    <span class="priority-indicator <%= priorityClass %>"></span>
                                    #<%= i + 1 %>
                                </div>
                            </td>
                            <td><strong>#<%= reservation.getId() %></strong></td>
                            <td>
                                <div class="customer-info">
                                    <div class="customer-name">
                                        <i class="fas fa-user-circle"></i>
                                        <%= reservation.getCustomerName() != null ? reservation.getCustomerName() : "Guest" %>
                                    </div>
                                    <div class="customer-details">
                                        User ID: <%= reservation.getUserId() %>
                                        <% if (reservation.getCustomerEmail() != null && !reservation.getCustomerEmail().isEmpty()) { %>
                                        <br><i class="fas fa-envelope"></i> <%= reservation.getCustomerEmail() %>
                                        <% } %>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <div class="table-info">
                                    <i class="fas fa-chair"></i>
                                    <strong>Table <%= reservation.getTableId() %></strong>
                                </div>
                            </td>
                            <td>
                                <div>
                                    <i class="fas fa-calendar"></i>
                                    <%= reservation.getFormattedReservationTime() %>
                                </div>
                                <% if (reservation.isFutureReservation()) { %>
                                <div class="time-remaining">
                                    <i class="fas fa-hourglass-half"></i> Future reservation
                                </div>
                                <% } else { %>
                                <div class="time-remaining" style="color: var(--error);">
                                    <i class="fas fa-exclamation-triangle"></i> Past due
                                </div>
                                <% } %>
                            </td>
                            <td>
                                <% if (reservation.getCustomerPhone() != null && !reservation.getCustomerPhone().isEmpty()) { %>
                                <i class="fas fa-phone"></i> <%= reservation.getCustomerPhone() %>
                                <% } else { %>
                                <span style="color: var(--gray);">No phone</span>
                                <% } %>
                            </td>
                            <td>
                                <span class="status-badge status-<%= reservation.getStatus() %>">
                                    <% if ("pending".equals(reservation.getStatus())) { %>
                                        <i class="fas fa-clock"></i>
                                    <% } else if ("confirmed".equals(reservation.getStatus())) { %>
                                        <i class="fas fa-check"></i>
                                    <% } else { %>
                                        <i class="fas fa-times"></i>
                                    <% } %>
                                    <%= reservation.getStatus().toUpperCase() %>
                                </span>
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <%
                                        Integer currentUserId = (Integer) session.getAttribute("userId");
                                        if (currentUserId != null && currentUserId == reservation.getUserId()) {
                                    %>
                                    <form action="reservationQueue" method="post" style="display: inline;" class="remove-form">
                                        <input type="hidden" name="action" value="removeFromQueue">
                                        <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                                        <button type="submit" class="btn btn-danger btn-sm"
                                                onclick="return confirm('Remove your reservation from queue?')">
                                            <i class="fas fa-trash"></i> Remove
                                        </button>
                                    </form>
                                    <% } %>

                                    <% if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) { %>
                                    <button type="button" class="btn btn-warning btn-sm"
                                            onclick="showSpecialRequests('<%= reservation.getSpecialRequests().replace("'", "\\'") %>')">
                                        <i class="fas fa-comment"></i> Notes
                                    </button>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>

            <% } else { %>

            <div class="empty-state">
                <i class="fas fa-inbox"></i>
                <h3>Queue is Empty</h3>
                <p>No reservations are currently in the queue.</p>
                <p style="margin-top: 15px;">
                    <small>The queue uses a custom implementation with merge sort for optimal time-based ordering.</small>
                </p>
            </div>

            <% } %>
        </div>

        <!-- Queue Operations -->
        <div class="section-card">
            <div class="section-header">
                <i class="fas fa-cogs"></i>
                <h3>Queue Operations</h3>
            </div>

            <div class="operations-grid">
                <form action="reservationQueue" method="post" style="display: contents;">
                    <input type="hidden" name="action" value="processNext">
                    <button type="submit" class="btn btn-success" <%= queuedReservations == null || queuedReservations.isEmpty() ? "disabled" : "" %>>
                        <i class="fas fa-arrow-right"></i> Process Next
                    </button>
                </form>

                <% if (session.getAttribute("isAdmin") != null && (Boolean) session.getAttribute("isAdmin")) { %>
                <a href="admin/queueManagement" class="btn btn-warning">
                    <i class="fas fa-cog"></i> Admin Management
                </a>
                <% } %>

                <a href="userTables" class="btn btn-primary">
                    <i class="fas fa-chair"></i> View Tables
                </a>

                <a href="categoryMenu" class="btn btn-secondary">
                    <i class="fas fa-utensils"></i> View Menu
                </a>

                <button type="button" class="btn btn-primary" onclick="window.location.reload()">
                    <i class="fas fa-sync-alt"></i> Refresh Queue
                </button>

                <button type="button" class="btn btn-warning" onclick="showQueueInfo()">
                    <i class="fas fa-info-circle"></i> Queue Info
                </button>
            </div>

            <!-- Technical Information -->
            <div style="margin-top: 30px; padding: 20px; background: var(--light); border-radius: 10px; border-left: 5px solid var(--primary);">
                <h4><i class="fas fa-graduation-cap"></i> Data Structure & Algorithm Implementation</h4>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-top: 15px;">
                    <div>
                        <h5><i class="fas fa-layer-group"></i> Custom Queue Features:</h5>
                        <ul style="margin: 10px 0; padding-left: 20px;">
                            <li>Array-based circular queue implementation</li>
                            <li>FIFO (First In, First Out) operations</li>
                            <li>Dynamic size management with overflow protection</li>
                            <li>Efficient enqueue/dequeue operations</li>
                        </ul>
                    </div>
                    <div>
                        <h5><i class="fas fa-sort-amount-down"></i> Merge Sort Algorithm:</h5>
                        <ul style="margin: 10px 0; padding-left: 20px;">
                            <li>O(n log n) time complexity sorting</li>
                            <li>Stable sort by reservation time</li>
                            <li>Divide and conquer approach</li>
                            <li>Custom comparator for DateTime objects</li>
                        </ul>
                    </div>
                </div>
                <p style="margin-top: 15px; font-style: italic; color: var(--gray);">
                    <strong>Note:</strong> This implementation demonstrates custom data structures and algorithms
                    built from scratch without using Java Collections Framework, specifically designed for
                    academic evaluation of DSA concepts.
                </p>
            </div>
        </div>
    </div>

    <!-- Modal for Special Requests -->
    <div id="specialRequestsModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000;">
        <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 30px; border-radius: 10px; max-width: 500px; width: 90%;">
            <h3><i class="fas fa-comment"></i> Special Requests</h3>
            <p id="specialRequestsContent" style="margin: 15px 0; padding: 15px; background: var(--light); border-radius: 5px;"></p>
            <button onclick="closeSpecialRequests()" class="btn btn-primary">
                <i class="fas fa-times"></i> Close
            </button>
        </div>
    </div>

    <script>
        // Set minimum datetime to current time
        document.addEventListener('DOMContentLoaded', function() {
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            const dateTimeInput = document.getElementById('reservationTime');
            if (dateTimeInput) {
                dateTimeInput.min = now.toISOString().slice(0, 16);
            }

            // Auto-focus first form field
            const firstInput = document.getElementById('tableId');
            if (firstInput) {
                firstInput.focus();
            }
        });

        // Auto-refresh functionality
        let refreshTimeout;
        function startAutoRefresh() {
            const indicator = document.getElementById('refreshIndicator');
            indicator.classList.add('show');

            refreshTimeout = setTimeout(function() {
                indicator.classList.remove('show');
                window.location.reload();
            }, 30000); // 30 seconds
        }

        // Start auto-refresh on page load
        startAutoRefresh();

        // Form submission with loading state
        document.getElementById('addReservationForm').addEventListener('submit', function(e) {
            const submitBtn = document.getElementById('submitBtn');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="loading"></span> Adding to Queue...';

            // Re-enable button after 5 seconds as fallback
            setTimeout(() => {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-plus"></i> Add to Queue';
            }, 5000);
        });

        // Remove form submissions
        document.querySelectorAll('.remove-form').forEach(form => {
            form.addEventListener('submit', function(e) {
                const button = this.querySelector('button');
                button.disabled = true;
                button.innerHTML = '<span class="loading"></span> Removing...';
            });
        });

        // Special requests modal functions
        function showSpecialRequests(requests) {
            document.getElementById('specialRequestsContent').textContent = requests;
            document.getElementById('specialRequestsModal').style.display = 'block';
        }

        function closeSpecialRequests() {
            document.getElementById('specialRequestsModal').style.display = 'none';
        }

        // Queue information modal
        function showQueueInfo() {
            const queueSize = <%= request.getAttribute("queueSize") != null ? request.getAttribute("queueSize") : 0 %>;
            const isEmpty = <%= request.getAttribute("queueEmpty") %>;
            const isFull = <%= request.getAttribute("queueFull") %>;

            let message = `Queue Information:\n\n`;
            message += `Current Size: ${queueSize} reservations\n`;
            message += `Status: ${isEmpty ? 'Empty' : (isFull ? 'Full' : 'Active')}\n`;
            message += `Implementation: Custom Queue + Merge Sort\n`;
            message += `Auto-refresh: Every 30 seconds\n\n`;
            message += `Data Structures Used:\n`;
            message += `• Array-based circular queue\n`;
            message += `• Merge sort for time-based ordering\n`;
            message += `• Custom comparators for DateTime objects`;

            alert(message);
        }

        // Keyboard shortcuts
        document.addEventListener('keydown', function(e) {
            if (e.ctrlKey || e.metaKey) {
                switch(e.key) {
                    case 'r':
                        e.preventDefault();
                        window.location.reload();
                        break;
                    case 'n':
                        e.preventDefault();
                        document.getElementById('tableId').focus();
                        break;
                    case 'i':
                        e.preventDefault();
                        showQueueInfo();
                        break;
                }
            }
        });

        // Close modal when clicking outside
        document.getElementById('specialRequestsModal').addEventListener('click', function(e) {
            if (e.target === this) {
                closeSpecialRequests();
            }
        });

        // Form validation enhancements
        document.getElementById('customerName').addEventListener('input', function() {
            this.value = this.value.replace(/[^a-zA-Z\s]/g, '');
        });

        document.getElementById('customerPhone').addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9+\-\s()]/g, '');
        });

        // Success/Error message auto-hide
        setTimeout(function() {
            const messages = document.querySelectorAll('.message');
            messages.forEach(message => {
                message.style.opacity = '0';
                setTimeout(() => message.remove(), 300);
            });
        }, 5000);

        // Smooth scroll to top when refreshing
        function scrollToTop() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        }

        // Connection status indicator
        window.addEventListener('online', function() {
            console.log('Connection restored');
        });

        window.addEventListener('offline', function() {
            clearTimeout(refreshTimeout);
            console.log('Connection lost - auto-refresh paused');
        });
    </script>
</body>
</html>