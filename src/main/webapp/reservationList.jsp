<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.restaurant_table_reservation.model.Reservation" %>
<%
    Reservation[] reservations = (Reservation[]) request.getAttribute("reservations");
%>
<table class="data-table">
    <tr>
        <th>Reservation ID</th>
        <th>User ID</th>
        <th>Table ID</th>
        <th>Reservation Date & Time</th>
    </tr>
    <%
        if (reservations != null && reservations.length > 0) {
            for (int i = 0; i < reservations.length; i++) {
                if (reservations[i] != null) {
    %>
    <tr>
        <td><%= reservations[i].getId() %></td>
        <td><%= reservations[i].getUserId() %></td>
        <td><%= reservations[i].getTableId() %></td>
        <td><%= reservations[i].getReservationTime() %></td>
    </tr>
    <%
            }
        }
    } else {
    %>
    <tr>
        <td colspan="4">No reservations found.</td>
    </tr>
    <%
        }
    %>
</table>