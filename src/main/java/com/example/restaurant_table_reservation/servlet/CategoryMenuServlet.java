package com.example.restaurant_table_reservation.servlet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.restaurant_table_reservation.model.MenuItem;
import com.example.restaurant_table_reservation.service.MenuService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/categoryMenu")
public class CategoryMenuServlet extends HttpServlet {
    private MenuService service = new MenuService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        List<MenuItem> items = service.getAllItems();
        System.out.println("Retrieved menu items: " + items); // Debug log

        // Group items by category
        Map<String, List<MenuItem>> itemsByCategory = items.stream()
                .collect(Collectors.groupingBy(
                    item -> item.getCategory() == null ? "Uncategorized" : item.getCategory(),
                    LinkedHashMap::new,
                    Collectors.toList()));

        req.setAttribute("itemsByCategory", itemsByCategory);
        req.getRequestDispatcher("categoryMenu.jsp").forward(req, resp);
    }
}
