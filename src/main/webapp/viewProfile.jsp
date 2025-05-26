<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>View Profile</title>
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

        .profile-header {
            margin-bottom: 30px;
        }

        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            margin-bottom: 20px;
            border: 3px solid #ff6347;
        }

        h2 {
            font-size: 36px;
            margin-bottom: 30px;
        }

        .profile-info {
            text-align: left;
            margin-bottom: 30px;
        }

        .info-group {
            margin-bottom: 20px;
        }

        .info-label {
            font-size: 16px;
            color: #ff6347;
            margin-bottom: 5px;
        }

        .info-value {
            font-size: 18px;
            padding: 10px;
            background-color: rgba(255, 255, 255, 0.1);
            border-radius: 5px;
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

        .btn-delete {
            background-color: #dc3545;
        }

        .btn-delete:hover {
            background-color: #c82333;
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
    </style>
</head>
<body>
<div class="overlay">
    <div class="profile-container">
        <div class="profile-header">
            <img src="https://ui-avatars.com/api/?name=${user.username}&background=ff6347&color=fff"
                 alt="Profile Avatar" class="profile-avatar">
            <h2>${user.username}'s Profile</h2>
        </div>

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

        <div class="profile-info">
            <div class="info-group">
                <div class="info-label">Username</div>
                <div class="info-value">${user.username}</div>
            </div>
            <div class="info-group">
                <div class="info-label">Email</div>
                <div class="info-value">${user.email}</div>
            </div>
        </div>

        <div class="button-group">
            <a href="${pageContext.request.contextPath}/EditProfileServlet" class="btn btn-edit">Edit Profile</a>
            <form action="${pageContext.request.contextPath}/DeleteProfileServlet" method="post" style="display:inline;">
                <button type="submit" class="btn btn-delete" onclick="return confirm('Are you sure you want to delete your profile? This action cannot be undone.');">Delete Profile</button>
            </form>
            <a href="${pageContext.request.contextPath}/categoryMenu" class="btn btn-back">Back to Menu</a>
        </div>
    </div>
</div>
</body>
</html>