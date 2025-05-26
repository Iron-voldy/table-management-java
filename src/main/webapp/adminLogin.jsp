<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Login</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-image:url("https://plus.unsplash.com/premium_photo-1661302846246-e8faef18255d?fm=jpg&q=60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8YWRtaW58ZW58MHx8MHx8fDA%3D");
            background-size: cover;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .login-container {
            background-color: rgba(0, 0, 0, 0.8);
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.3);
            width: 350px;
        }
        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #ffffff;
            font-size: 28px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #ffffff;
            font-weight: 500;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 12px;
            margin-bottom: 20px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 16px;
            box-sizing: border-box;
        }
        button {
            width: 100%;
            padding: 14px;
            background-color: #007bff;
            border: none;
            color: white;
            font-size: 18px;
            border-radius: 6px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        button:hover {
            background-color: #0056b3;
        }
        .error {
            color: #ff6b6b;
            margin-bottom: 20px;
            text-align: center;
            background-color: rgba(255, 107, 107, 0.1);
            padding: 10px;
            border-radius: 5px;
            border-left: 4px solid #ff6b6b;
        }
        .back-link {
            text-align: center;
            margin-top: 20px;
        }
        .back-link a {
            color: #007bff;
            text-decoration: none;
            font-size: 14px;
        }
        .back-link a:hover {
            text-decoration: underline;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .admin-info {
            background-color: rgba(0, 123, 255, 0.1);
            border-left: 4px solid #007bff;
            padding: 15px;
            margin-bottom: 25px;
            border-radius: 5px;
        }
        .admin-info h3 {
            color: #007bff;
            margin-bottom: 10px;
            font-size: 16px;
        }
        .admin-info p {
            color: #ffffff;
            font-size: 14px;
            margin: 5px 0;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2>Admin Login</h2>

    <!-- Admin Login Information -->
    <div class="admin-info">
        <h3>Default Admin Credentials</h3>
        <p><strong>Username:</strong> admin</p>
        <p><strong>Password:</strong> admin</p>
    </div>

    <form action="AdminLoginServlet" method="POST">
        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required placeholder="Enter admin username">
        </div>

        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required placeholder="Enter admin password">
        </div>

        <button type="submit">Login as Admin</button>
    </form>

    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
    <div class="error">
        <strong>Error:</strong> <%= error %>
    </div>
    <% } %>

    <div class="back-link">
        <a href="index.jsp">← Back to Home</a>
    </div>
</div>

<script>
    // Auto-focus on username field
    document.getElementById('username').focus();

    // Form validation
    document.querySelector('form').addEventListener('submit', function(e) {
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!username || !password) {
            e.preventDefault();
            alert('Please enter both username and password');
            return false;
        }
    });
</script>
</body>
</html>