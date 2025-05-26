<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.example.restaurant_table_reservation.model.Reservation, com.example.restaurant_table_reservation.model.User, com.example.restaurant_table_reservation.model.Table, com.example.restaurant_table_reservation.service.UserService, com.example.restaurant_table_reservation.service.TableService" %>
<html>
<head>
    <title>Admin Queue Management</title>
    <style>
        :root {
            --primary: #5c6bc0;
            --primary-dark: #3949ab;
            --accent: #ff7043;
            --light: #f8f9fa;
            --dark: #212529;
            --gray: #6c757d;
            --border: #e0e0e0;
            --success: #66bb6a;
            --warning: #ffa726;
            --error: #ef5350;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Roboto', -apple-system, BlinkMacSystemFont, 'Segoe UI', Oxygen, Ubuntu, Cantarell, sans-serif;
        }

        body {
            background: var(--light);
            color: var(--dark);
            line-height: 1.6;
        }

        .admin-container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }

        .admin-header {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            text-align: center;
        }

        .admin-header h1 {
            font-size: 2.5rem;
            margin-bottom: 10px;
        }

        .admin-header p {
            font-size: 1.1rem;
            opacity: 0.9;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }

        .stat-card:hover {
            transform: translateY(-5px);
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
        }

        .stat-card-icon.queue-size {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
        }

        .stat-card-icon.pending {
            background: linear-gradient(135deg, var(--warning), #ff8f00);
        }

        .stat-card-icon.confirmed {
            background: linear-gradient(135deg, var(--success), #388e3c);
        }

        .stat-card-icon.operations {
            background: linear-gradient(135deg, var(--accent), #e64a19);
        }

        .stat-card h3 {
            color: var(--gray);
            font-size: 0.9rem;
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .stat-card .stat-number {
            font-size: 2rem;
            font-weight: 700;
            color: var(--dark);
            margin: 10px 0;
        }

        .control-panel {
            background: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .control-panel h3 {
            margin-bottom: 20px;
            color: var(--primary);
            border-bottom: 2px solid var(--border);
            padding-bottom: 10px;
        }

        .btn-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }

        .btn {
            padding: 12px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.3s;
            text-decoration: none;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            color: white;
        }

        .btn-primary {
            background: var(--primary);
        }

        .btn-primary:hover {
            background: var(--primary-dark);
            transform: translateY(-2px);
        }

        .btn-success {
            background: var(--success);
        }

        .btn-warning {
            background: var(--warning);
        }

        .btn-danger {
            background: var(--error);
        }

        .btn-secondary {
            background: var(--gray);
        }

        .queue-section {
            background: white;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .queue-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .queue-table th,
        .queue-table td {
            padding: 15px 12px;
            text-align: left;
            border-bottom: 1px solid var(--border);
        }

        .queue-table th {
            background: var(--primary);
            color: white;
            font-weight: 500;
            position: sticky;
            top: 0;
        }

        .queue-table tr:hover {
            background: var(--light);
        }

        .status-badge {
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }

        .status-pending {
            background: #fff3e0;
            color: #e65100;
        }

        .status-confirmed {
            background: #e8f5e9;
            color: #2e7d32;
        }

        .status-cancelled {
            background: #ffebee;
            color: #c62828;
        }

        .action-buttons {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }

        .btn-sm {
            padding: 6px 12px;
            font-size: 12px;
        }

        .message {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .message.success {
            background: #e8f5e9;
            color: #2e7d32;
            border-left: 4px solid var(--success);
        }

        .message.error {
            background: #ffebee;
            color: #c62828;
            border-left: 4px solid var(--error);
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
        }

        .nav-breadcrumb {
            margin-bottom: 20px;
        }

        .nav-breadcrumb a {
            color: var(--primary);
            text-decoration: none;
            font-weight: 500;
        }

        .nav-breadcrumb a:hover {
            text-decoration: underline;
        }

        @media (max-width: 768px) {
            .btn-grid {
                grid-template-columns: 1fr;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .queue-table {
                font-size: 14px;
            }

            .action-buttons {
                flex-direction: column;
            }
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<div class="admin-container">
    <!-- Navigation Breadcrumb -->
    <div class="nav-breadcrumb">
        <a href="dashboard"><i class="fas fa-home"></i> Admin Dashboard</a>
        <span> / </span>
        <span>Queue Management</span>
    </div>

    <!-- Admin Header -->
    <div class="admin-header">
        <h1><i class="fas fa-cogs"></i> Queue Management System</h1>
        <p>Manage and monitor reservation queue with custom data structures</p>
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

    <!-- Statistics Grid -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-card-header">
                <h3>Queue Size</h3>
                <div class="stat-card-icon queue-size">
                    <i class="fas fa-list"></i>
                </div>
            </div>
            <div class="stat-number"><%= request.getAttribute("queueSize") %></div>
            <p>Total reservations in queue</p>
        </div>

        <div class="stat-card">
            <div class="stat-card-header">
                <h3>Queue Status</h3>
                <div class="stat-card-icon pending">
                    <i class="fas fa-clock"></i>
                </div>
            </div>
            <div class="stat-number">
                <%= (Boolean) request.getAttribute("queueEmpty") ? "Empty" : "Active" %>
            </div>
            <p>Current queue state</p>
        </div>

        <div class="stat-card">
            <div class="stat-card-header">
                <h3>Capacity</h3>
                <div class="stat-card-icon confirmed">
                    <i class="fas fa-chart-pie"></i>
                </div>
            </div>
            <div class="stat-number">
                <%= (Boolean) request.getAttribute("queueFull") ? "Full" : "Available" %>
            </div>
            <p>Queue capacity status</p>
        </div>

        <div class="stat-card">
            <div class="stat-card-header">
                <h3>DSA Features</h3>
                <div class="stat-card-icon operations">
                    <i class="fas fa-code"></i>
                </div>
            </div>
            <div class="stat-number">Custom</div>
            <p>Queue + Merge Sort</p>
        </div>
    </div>

    <!-- Control Panel -->
    <div class="control-panel">
        <h3><i class="fas fa-control"></i> Queue Operations</h3>
        <div class="btn-grid">
            <form action="queueManagement" method="post" style="display: contents;">
                <input type="hidden" name="action" value="processNext">
                <button type="submit" class="btn btn-success">
                    <i class="fas fa-arrow-right"></i> Process Next Reservation
                </button>
            </form>

            <form action="queueManagement" method="post" style="display: contents;">
                <input type="hidden" name="action" value="reorderQueue">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-sort"></i> Sort Queue (Merge Sort)
                </button>
            </form>

            <form action="queueManagement" method="post" style="display: contents;">
                <input type="hidden" name="action" value="viewStatistics">
                <button type="submit" class="btn btn-warning">
                    <i class="fas fa-chart-bar"></i> View Statistics
                </button>
            </form>

            <form action="queueManagement" method="post" style="display: contents;"
                  onsubmit="return confirm('Are you sure you want to clear the entire queue?')">
                <input type="hidden" name="action" value="clearQueue">
                <button type="submit" class="btn btn-danger">
                    <i class="fas fa-trash"></i> Clear Queue
                </button>
            </form>

            <form action="queueManagement" method="post" style="display: contents;">
                <input type="hidden" name="action" value="demonstrateSort">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-play"></i> Demonstrate Sort
                </button>
            </form>

            <form action="queueManagement" method="post" style="display: contents;">
                <input type="hidden" name="action" value="sortReservations">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-sort-amount-down"></i> Sort All Reservations
                </button>
            </form>
        </div>
    </div>

    <!-- Queue Display -->
    <div class="queue-section">
        <h3><i class="fas fa-list-ol"></i> Current Reservation Queue</h3>
        <p style="margin-bottom: 20px; color: var(--gray);">
            <strong>Data Structure:</strong> Custom Queue Implementation with Merge Sort Algorithm
        </p>

        <%
            List<Reservation> queuedReservations = (List<Reservation>) request.getAttribute("queuedReservations");
            UserService userService = (UserService) request.getAttribute("userService");
            TableService tableService = (TableService) request.getAttribute("tableService");

            if (queuedReservations != null && !queuedReservations.isEmpty()) {
        %>

        <table class="queue-table">
            <thead>
                <tr>
                    <th><i class="fas fa-hashtag"></i> Position</th>
                    <th><i class="fas fa-id-card"></i> Reservation ID</th>
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

                        // Get user information
                        String customerName = reservation.getCustomerName();
                        if (customerName == null && userService != null) {
                            List<User> users = userService.getAllUsers();
                            for (User user : users) {
                                if (user.getId() == reservation.getUserId()) {
                                    customerName = user.getUsername();
                                    break;
                                }
                            }
                        }

                        // Get table information
                        String tableInfo = "Table " + reservation.getTableId();
                        if (tableService != null) {
                            Table[] tables = tableService.getAllTables();
                            for (Table table : tables) {
                                if (table.getId() == reservation.getTableId()) {
                                    tableInfo = "Table " + table.getNumber() + " (" + table.getCapacity() + " seats)";
                                    break;
                                }
                            }
                        }
                %>
                <tr>
                    <td><strong><%= i + 1 %></strong></td>
                    <td>#<%= reservation.getId() %></td>
                    <td>
                        <strong><%= customerName != null ? customerName : "Unknown" %></strong><br>
                        <small>User ID: <%= reservation.getUserId() %></small>
                    </td>
                    <td><%= tableInfo %></td>
                    <td>
                        <div><%= reservation.getReservationTime() %></div>
                    </td>
                    <td>
                        <%= reservation.getCustomerPhone() != null ? reservation.getCustomerPhone() : "N/A" %>
                    </td>
                    <td>
                        <span class="status-badge status-<%= reservation.getStatus() %>">
                            <%= reservation.getStatus().toUpperCase() %>
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <% if ("pending".equals(reservation.getStatus())) { %>
                            <form action="queueManagement" method="post" style="display: inline;">
                                <input type="hidden" name="action" value="confirmReservation">
                                <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                                <button type="submit" class="btn btn-success btn-sm">
                                    <i class="fas fa-check"></i> Confirm
                                </button>
                            </form>
                            <% } %>

                            <form action="queueManagement" method="post" style="display: inline;">
                                <input type="hidden" name="action" value="cancelReservation">
                                <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                                <button type="submit" class="btn btn-danger btn-sm"
                                        onclick="return confirm('Cancel this reservation?')">
                                    <i class="fas fa-times"></i> Cancel
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>

        <% } else { %>

        <div class="empty-state">
            <i class="fas fa-inbox"></i>
            <h3>Queue is Empty</h3>
            <p>No reservations are currently in the queue.</p>
            <p style="margin-top: 10px;">
                <a href="../reservationQueue" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Add Reservation to Queue
                </a>
            </p>
        </div>

        <% } %>
    </div>

    <!-- Technical Information -->
    <div class="control-panel">
        <h3><i class="fas fa-info-circle"></i> Technical Implementation</h3>
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px;">
            <div style="padding: 15px; background: var(--light); border-radius: 8px;">
                <h4><i class="fas fa-layer-group"></i> Queue Data Structure</h4>
                <ul style="margin: 10px 0; padding-left: 20px;">
                    <li>Custom array-based circular queue</li>
                    <li>FIFO (First In, First Out) operations</li>
                    <li>Enqueue and Dequeue functionality</li>
                    <li>Automatic size management</li>
                </ul>
            </div>
            <div style="padding: 15px; background: var(--light); border-radius: 8px;">
                <h4><i class="fas fa-sort-amount-down"></i> Merge Sort Algorithm</h4>
                <ul style="margin: 10px 0; padding-left: 20px;">
                    <li>Divide and conquer approach</li>
                    <li>O(n log n) time complexity</li>
                    <li>Stable sorting by reservation time</li>
                    <li>Custom comparator implementation</li>
                </ul>
            </div>
        </div>
        <div style="margin-top: 15px; padding: 15px; background: #e3f2fd; border-radius: 8px; border-left: 4px solid var(--primary);">
            <strong><i class="fas fa-graduation-cap"></i> DSA Implementation Note:</strong>
            This system implements custom Queue and Merge Sort algorithms from scratch without using Java Collections Framework,
            demonstrating fundamental data structure and algorithm concepts for academic evaluation.
        </div>
    </div>
</div>

<script>
    // Auto-refresh every 30 seconds
    setTimeout(function() {
        window.location.reload();
    }, 30000);

    // Add loading state to buttons
    document.querySelectorAll('button[type="submit"]').forEach(button => {
        button.addEventListener('click', function() {
            this.disabled = true;
            const originalText = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';

            // Re-enable after 3 seconds as fallback
            setTimeout(() => {
                this.disabled = false;
                this.innerHTML = originalText;
            }, 3000);
        });
    });

    // Confirmation dialogs
    document.querySelectorAll('form').forEach(form => {
        const action = form.querySelector('input[name="action"]')?.value;
        if (action === 'clearQueue') {
            form.addEventListener('submit', function(e) {
                if (!confirm('This will remove all reservations from the queue. Are you sure?')) {
                    e.preventDefault();
                }
            });
        }
    });
</script>

</body>
</html>