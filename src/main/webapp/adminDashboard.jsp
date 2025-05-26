<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.example.restaurant_table_reservation.model.User, com.example.restaurant_table_reservation.model.Reservation, com.example.restaurant_table_reservation.model.Table, com.example.restaurant_table_reservation.service.UserService, com.example.restaurant_table_reservation.service.TableService" %>
<%
    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    Integer todaysOrdersCount = (Integer) request.getAttribute("todaysOrdersCount");
    Long availableTablesCount = (Long) request.getAttribute("availableTablesCount");
    Integer totalTablesCount = (Integer) request.getAttribute("totalTablesCount");
    Double todaysRevenue = (Double) request.getAttribute("todaysRevenue");
    List<User> users = (List<User>) request.getAttribute("users");
    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
    UserService userService = (UserService) request.getAttribute("userService");
    TableService tableService = (TableService) request.getAttribute("tableService");
%>
<html>
<head>
    <title>Admin Dashboard</title>
    <style>
        /* Modern Admin Dashboard CSS */
        :root {
            --primary: #5c6bc0;
            --primary-dark: #3949ab;
            --primary-light: #7986cb;
            --accent: #ff7043;
            --text: #263238;
            --text-light: #546e7a;
            --background: #f5f7fa;
            --card-bg: #ffffff;
            --success: #66bb6a;
            --warning: #ffa726;
            --error: #ef5350;
            --border: #e0e0e0;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Roboto', -apple-system, BlinkMacSystemFont, 'Segoe UI', Oxygen, Ubuntu, Cantarell, sans-serif;
        }

        body {
            background: var(--background) !important;
            background-color: var(--background) !important;
            color: var(--text);
            line-height: 1.6;
        }

        .admin-container {
            display: grid;
            grid-template-columns: 240px 1fr;
            min-height: 100vh;
        }

        /* Sidebar Styles */
        .admin-sidebar {
            background: var(--primary);
            color: white;
            padding: 1.5rem 0;
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
        }

        .admin-brand {
            padding: 0 1.5rem 1.5rem;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            margin-bottom: 1.5rem;
        }

        .admin-brand h1 {
            font-size: 1.3rem;
            font-weight: 600;
            display: flex;
            align-items: center;
        }

        .admin-brand h1 i {
            margin-right: 10px;
            color: var(--accent);
        }

        .admin-nav ul {
            list-style: none;
        }

        .admin-nav li a {
            display: flex;
            align-items: center;
            padding: 0.8rem 1.5rem;
            color: rgba(255,255,255,0.9);
            text-decoration: none;
            transition: all 0.2s;
        }

        .admin-nav li a:hover,
        .admin-nav li a.active {
            background: rgba(255,255,255,0.1);
            color: white;
            border-left: 3px solid var(--accent);
        }

        .admin-nav li a i {
            margin-right: 12px;
            width: 20px;
            text-align: center;
        }

        /* Main Content Styles */
        .admin-main {
            padding: 2rem;
        }

        .admin-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }

        .admin-title h2 {
            font-size: 1.5rem;
            color: var(--text);
            font-weight: 500;
        }

        .admin-title p {
            color: var(--text-light);
            font-size: 0.9rem;
            margin-top: 0.3rem;
        }

        .admin-user {
            display: flex;
            align-items: center;
        }

        .admin-user img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 10px;
            object-fit: cover;
        }

        .admin-user-info small {
            display: block;
            color: var(--text-light);
            font-size: 0.8rem;
            margin-top: 2px;
        }

        /* Dashboard Cards */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: var(--card-bg);
            border-radius: 8px;
            padding: 1.5rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            transition: transform 0.3s;
        }

        .stat-card:hover {
            transform: translateY(-5px);
        }

        .stat-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }

        .stat-card-icon {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
        }

        .stat-card-icon.users {
            background: linear-gradient(135deg, #9575cd, #7e57c2);
        }

        .stat-card-icon.orders {
            background: linear-gradient(135deg, #64b5f6, #42a5f5);
        }

        .stat-card-icon.tables {
            background: linear-gradient(135deg, #4db6ac, #26a69a);
        }

        .stat-card-icon.revenue {
            background: linear-gradient(135deg, #ffb74d, #ffa726);
        }

        .stat-card h3 {
            font-size: 1.1rem;
            color: var(--text-light);
            font-weight: 500;
        }

        .stat-card h2 {
            font-size: 1.8rem;
            margin: 0.5rem 0;
        }

        .stat-card p {
            color: var(--text-light);
            font-size: 0.85rem;
        }

        /* Content Sections */
        .content-section {
            background: var(--card-bg);
            border-radius: 8px;
            padding: 1.5rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            margin-bottom: 2rem;
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
            padding-bottom: 0.8rem;
            border-bottom: 1px solid var(--border);
        }

        .section-header h3 {
            font-size: 1.2rem;
            font-weight: 500;
        }

        .section-header a {
            color: var(--primary);
            text-decoration: none;
            font-size: 0.9rem;
            display: flex;
            align-items: center;
        }

        .section-header a i {
            margin-left: 5px;
        }

        /* Tables - Updated with black text */
        .data-table {
            width: 100%;
            border-collapse: collapse;
            color: #000000;
        }

        .data-table th {
            text-align: left;
            padding: 0.8rem;
            color: var(--text-light);
            font-weight: 500;
            font-size: 0.85rem;
            text-transform: uppercase;
            border-bottom: 1px solid var(--border);
        }

        .data-table td {
            padding: 0.8rem;
            border-bottom: 1px solid var(--border);
            font-size: 0.9rem;
            color: #000000 !important;
        }

        .data-table tr:last-child td {
            border-bottom: none;
        }

        .action-btn {
            padding: 0.3rem 0.6rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-right: 0.5rem;
            text-decoration: none;
            color: white;
            font-size: 0.85rem;
        }

        .edit-btn {
            background-color: #ff9500;
        }

        .delete-btn {
            background-color: var(--error);
        }

        /* Responsive */
        @media (max-width: 992px) {
            .admin-container {
                grid-template-columns: 1fr;
            }

            .admin-sidebar {
                display: none;
            }
        }

        /* Specific style adjustments for the reservations table */
         .reservations-table th {
             background-color: #4CAF50; /* Green header */
             color: white; /* White text */
             text-align: center;
         }

         .reservations-table td {
             text-align: center;
         }
    </style>
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Roboto font -->
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
</head>
<body>
<div class="admin-container">
    <!-- Sidebar Navigation -->
    <aside class="admin-sidebar">
        <div class="admin-brand">
            <h1><i class="fas fa-utensils"></i> Restaurant Admin</h1>
        </div>
        <!-- In the sidebar navigation -->
        <nav class="admin-nav">
            <ul>
                <li><a href="#" class="active"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                <li><a href="#"><i class="fas fa-users"></i> Users</a></li>
                <li><a href="#"><i class="fas fa-clipboard-list"></i> Orders</a></li>
                <li><a href="#"><i class="fas fa-chair"></i> Tables</a></li>

                <!-- FIXED QUEUE MANAGEMENT LINK -->
                <li><a href="${pageContext.request.contextPath}/admin/queueManagement"><i class="fas fa-clock"></i> Queue Management</a></li>

                <li><a href="#"><i class="fas fa-utensils"></i> Menu</a></li>
                <li><a href="#"><i class="fas fa-comment-alt"></i> Feedback</a></li>
            </ul>
        </nav>
    </aside>

    <!-- Main Content Area -->
    <main class="admin-main">
        <header class="admin-header">
            <div class="admin-title">
                <h2>Dashboard Overview</h2>
                <p>Welcome back! Here's what's happening with your restaurant today.</p>
            </div>
            <div class="admin-user">
                <img src="https://randomuser.me/api/portraits/women/44.jpg" alt="Admin User">
                <div class="admin-user-info">
                    <strong>Admin User</strong>
                    <small>Restaurant Manager</small>
                </div>
            </div>
        </header>

        <!-- Stats Cards -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-card-header">
                    <h3>Total Users</h3>
                    <div class="stat-card-icon users">
                        <i class="fas fa-users"></i>
                    </div>
                </div>
                <h2><%= totalUsers %></h2>
            </div>
            <div class="stat-card">
                <div class="stat-card-header">
                    <h3>Today's Orders</h3>
                    <div class="stat-card-icon orders">
                        <i class="fas fa-clipboard-list"></i>
                    </div>
                </div>
                <h2><%= todaysOrdersCount %></h2>
            </div>
            <div class="stat-card">
                <div class="stat-card-header">
                    <h3>Available Tables</h3>
                    <div class="stat-card-icon tables">
                        <i class="fas fa-chair"></i>
                    </div>
                </div>
                <h2><%= availableTablesCount %>/<%= totalTablesCount %></h2>
            </div>
            <div class="stat-card">
                <div class="stat-card-header">
                    <h3>Today's Revenue</h3>
                    <div class="stat-card-icon revenue">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                </div>
                <h2>$<%= String.format("%.2f", todaysRevenue) %></h2>
            </div>
        </div>

        <!-- Quick Actions Section -->
        <div class="content-section">
            <div class="section-header">
                <h3><i class="fas fa-bolt"></i> Quick Actions</h3>
            </div>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                <a href="${pageContext.request.contextPath}/admin/queueManagement" style="padding: 1rem; background-color: var(--primary); color: white; border-radius: 8px; text-decoration: none; font-weight: 500; text-align: center; transition: all 0.3s;">
                    <i class="fas fa-clock"></i> Queue Management
                </a>
                <form action="${pageContext.request.contextPath}/admin/dashboard" method="post" style="display: contents;">
                    <input type="hidden" name="action" value="sortReservations">
                    <button type="submit" style="padding: 1rem; background-color: var(--success); color: white; border: none; border-radius: 8px; font-weight: 500; cursor: pointer; transition: all 0.3s;">
                        <i class="fas fa-sort"></i> Sort Reservations
                    </button>
                </form>
                <form action="${pageContext.request.contextPath}/admin/dashboard" method="post" style="display: contents;">
                    <input type="hidden" name="action" value="demonstrateSort">
                    <button type="submit" style="padding: 1rem; background-color: var(--warning); color: white; border: none; border-radius: 8px; font-weight: 500; cursor: pointer; transition: all 0.3s;">
                        <i class="fas fa-play"></i> Demo Merge Sort
                    </button>
                </form>
            </div>
        </div>

        <!-- Recent Orders Section -->
        <div class="content-section">
            <div class="section-header">
                <h3><i class="fas fa-clipboard-list"></i> Recent Orders</h3>
                <a href="#">View All <i class="fas fa-chevron-right"></i></a>
            </div>
            <jsp:include page="showOrders.jsp" />
        </div>

        <!-- Users Section -->
        <div class="content-section">
            <div class="section-header">
                <h3><i class="fas fa-users"></i> Registered Users</h3>
                <a href="showUsers" style="color: var(--primary); text-decoration: none; font-size: 0.9rem;">View All <i class="fas fa-chevron-right"></i></a>
            </div>
            <table class="data-table">
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Actions</th>
                </tr>
                <% if (users != null && !users.isEmpty()) {
                    for (User user : users) { %>
                <tr>
                    <td><%= user.getId() %></td>
                    <td><%= user.getUsername() %></td>
                    <td><%= user.getEmail() != null ? user.getEmail() : "N/A" %></td>
                    <td>
                        <a href="editUser?id=<%= user.getId() %>" class="action-btn edit-btn">Edit</a>
                        <a href="deleteUser?id=<%= user.getId() %>" class="action-btn delete-btn">Delete</a>
                    </td>
                </tr>
                <% } } else { %>
                <tr>
                    <td colspan="4">No users found.</td>
                </tr>
                <% } %>
            </table>
        </div>

        <!-- Reservations Section -->
        <div class="content-section">
            <div class="section-header">
                <h3><i class="fas fa-calendar-alt"></i> Reservations</h3>
                <a href="adminReservations" class="view-all-link">View All</a>
            </div>
            <div class="card">
                <div class="card-body">
                    <table class="data-table reservations-table">
                        <thead>
                            <tr>
                                <th>RESERVATION ID</th>
                                <th>USER</th>
                                <th>TABLE NUMBER</th>
                                <th>RESERVATION DATE & TIME</th>
                                <!-- Add more headers if needed (e.g., actions like cancel) -->
                            </tr>
                        </thead>
                        <tbody id="reservations-table-body">
                             <%-- Reservations will be loaded here via JavaScript --%>
                             <tr>
                                <td colspan="4">Loading reservations...</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Tables Section -->
        <div class="content-section">
            <div class="section-header">
                <h3><i class="fas fa-chair"></i> Table Status</h3>
                <a href="#">Manage Tables <i class="fas fa-chevron-right"></i></a>
            </div>
            <jsp:include page="tables.jsp" />
        </div>

        <!-- Manage Feedback and Menus Section -->
        <div class="content-section">
            <div class="section-header">
                <h3>Manage Feedback and Menus</h3>
            </div>
            <div style="display: flex; gap: 1rem;">
                <a href="reviews" style="flex: 1; text-align: center; padding: 1rem; background-color: var(--primary); color: white; border-radius: 8px; text-decoration: none; font-weight: 500; transition: background-color 0.3s;">
                    Manage Feedback
                </a>
                <a href="menu" style="flex: 1; text-align: center; padding: 1rem; background-color: var(--primary); color: white; border-radius: 8px; text-decoration: none; font-weight: 500; transition: background-color 0.3s;">
                    Manage Menus
                </a>
            </div>
        </div>
    </main>
</div>

<script>
    async function fetchReservations() {
        try {
            const response = await fetch('${pageContext.request.contextPath}/api/admin/reservations');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const reservations = await response.json();
            const tbody = document.getElementById('reservations-table-body');
            tbody.innerHTML = ''; // Clear existing rows

            if (reservations.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4">No reservations found.</td></tr>';
            } else {
                reservations.forEach(reservation => {
                    const row = `
                        <tr>
                            <td>${reservation.id}</td>
                            <td>${reservation.username}</td>
                            <td>${reservation.tableNumber}</td>
                            <td>${reservation.reservationTime}</td>
                        </tr>
                    `;
                    tbody.innerHTML += row;
                });
            }
        } catch (error) {
            console.error('Error fetching reservations:', error);
            const tbody = document.getElementById('reservations-table-body');
            tbody.innerHTML = '<tr><td colspan="4" style="color: red;">Error loading reservations.</td></tr>';
        }
    }

    // Fetch reservations when the page loads
    fetchReservations();

    // Fetch reservations every 10 seconds (adjust the interval as needed)
    setInterval(fetchReservations, 10000); // 10000 milliseconds = 10 seconds
</script>

</body>
</html>