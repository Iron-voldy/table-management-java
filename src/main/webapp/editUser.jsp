<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit User</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: url('https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?fm=jpg&q=60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8cmVzdGF1cmFudHN8ZW58MHx8MHx8fDA%3D') no-repeat center center/cover;
            color: white;
        }

        .overlay {
            background-color: rgba(0, 0, 0, 0.6);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 20px;
        }

        .profile-container {
            background-color: rgba(255, 255, 255, 0.1);
            padding: 30px;
            border-radius: 10px;
            width: 80%;
            max-width: 500px;
        }

        h1, h2 {
            font-size: 36px;
            margin-bottom: 30px;
        }

        p {
            font-size: 18px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 20px;
            text-align: left;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-size: 16px;
        }

        input[type="text"],
        input[type="password"],
        input[type="email"] {
            width: 100%;
            padding: 10px;
            border: none;
            border-radius: 5px;
            background-color: rgba(255, 255, 255, 0.9);
            font-size: 16px;
        }

        .button-group {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
        }

        .btn {
            padding: 12px 24px;
            font-size: 16px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            transition: background-color 0.3s ease;
            text-decoration: none;
            color: white;
        }

        .btn-edit {
            background-color: #ff6347;
        }

        .btn-edit:hover {
            background-color: #ff4500;
        }

        .btn-back {
            background-color: #6c757d;
        }

        .btn-back:hover {
            background-color: #5a6268;
        }

        .message {
            margin: 20px 0;
            padding: 10px;
            border-radius: 5px;
        }

        .success {
            background-color: rgba(40, 167, 69, 0.8);
        }

        .error {
            background-color: rgba(220, 53, 69, 0.8);
        }

        @media (max-width: 600px) {
            h1, h2 {
                font-size: 28px;
            }

            p {
                font-size: 16px;
            }

            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<div class="overlay">
    <div class="profile-container">
        <h2>Edit User Profile</h2>

        <% if (request.getAttribute("message") != null) { %>
            <div class="message success">
                <%= request.getAttribute("message") %>
            </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="message error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form method="post" action="editUser" class="edit-form">
            <input type="hidden" name="id" value="${user.id}" />

            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" value="${user.username}" required />
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" value="${user.password}" required />
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="${user.email}" required />
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-edit">Update Profile</button>
            </div>
        </form>
            <%
                // Check if user is admin or regular user
                boolean isAdmin = session.getAttribute("adminUser") != null;
                if (isAdmin) {
            %>
                <div class="button-group" style="margin-top: 20px;">
                    <a href="showUsers" class="btn btn-back">Back to Users List</a>
                </div>
            <% } else { %>
                <div class="button-group" style="margin-top: 20px;">
                    <a href="viewProfile" class="btn btn-back">Back to Profile</a>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
