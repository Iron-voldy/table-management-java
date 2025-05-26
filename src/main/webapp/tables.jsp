<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.restaurant_table_reservation.model.Table" %>
<% Table[] tables = (Table[]) request.getAttribute("tables"); %>
<table class="data-table">
    <tr>
        <th>ID</th>
        <th>Number</th>
        <th>Capacity</th>
        <th>Available</th>
        <th>Category</th>
    </tr>
    <% if (tables != null && tables.length > 0) {
        for (int i = 0; i < tables.length; i++) {
            if (tables[i] != null) {
    %>
    <tr>
        <td><%= tables[i].getId() %></td>
        <td><%= tables[i].getNumber() %></td>
        <td><%= tables[i].getCapacity() %></td>
        <td><%= tables[i].isAvailable() %></td>
        <td><%= tables[i].getCategory() %></td>
    </tr>
    <%
            }
        }
    } else { %>
    <tr>
        <td colspan="5">No tables found.</td>
    </tr>
    <% } %>
</table>