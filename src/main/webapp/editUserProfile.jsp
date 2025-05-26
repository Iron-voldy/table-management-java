<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Profile</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: url('https://images.unsplash.com/photo-1528605248644-14dd04022da1') no-repeat center center/cover;
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

        .form-group {
            margin-bottom: 20px;
            text-align: left;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-size: 16px;
            color: #ff6347;
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
            color: #333;
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

        .btn-update {
            background-color: #ff6347;
        }

        .btn-update:hover {
            background-color: #ff4500;
        }

        .btn-cancel {
            background-color: #6c757d;
        }

        .btn-cancel:hover {
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
            background-color: rgb(32, 154, 34);
        }

        @media (max-width: 600px) {
            h2 {
                font-size: 28px;
            }

            .profile-container {
                width: 95%;
                padding: 20px;
            }

            .profile-avatar {
                width: 100px;
                height: 100px;
            }

            .button-group {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<div class="overlay">
    <div class="profile-container">
        <div class="profile-header">
            <img src="https://ui-avatars.com/api/?name=${user.username}&background=ff6347&color=fff"
                 alt="Profile Avatar" class="profile-avatar">
            <h2>Edit Your Profile</h2>
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

        <form method="post" action="${pageContext.request.contextPath}/EditProfileServlet">
            <input type="hidden" name="id" value="${user.id}">

            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" value="${user.username}" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" value="${user.password}" required>
            </div>

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="${user.email}" required>
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-update">Update Profile</button>
                <a href="${pageContext.request.contextPath}/viewProfile" class="btn btn-cancel">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>