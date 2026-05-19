package com.fiveam.findmycomponent.controller;
import com.fiveam.findmycomponent.dao.OrderDao;
import com.fiveam.findmycomponent.dao.OrderDaoImpl;
import com.fiveam.findmycomponent.dao.OrderItemDao;
import com.fiveam.findmycomponent.dao.OrderItemDaoImpl;
import com.fiveam.findmycomponent.entity.Order;
import com.fiveam.findmycomponent.entity.OrderItem;
import com.fiveam.findmycomponent.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/seller/sales")
public class SellerSalesServlet extends HttpServlet {

    private OrderItemDao orderItemDao;
    private OrderDao orderDao;

    @Override
    public void init() throws ServletException {
        orderItemDao = new OrderItemDaoImpl();
        orderDao = new OrderDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User seller = (User) session.getAttribute("user");

        // Get all order items for this seller
        List<OrderItem> orderItems = orderItemDao.findBySellerId(seller.getId());

        // Calculate metrics
        double totalSales = calculateTotalSales(orderItems);
        int totalOrders = calculateTotalOrders(orderItems);
        int totalItemsSold = calculateTotalItemsSold(orderItems);
        int pendingOrders = calculatePendingOrders(orderItems);

        // Build product sales data
        Map<Integer, ProductSales> productSalesMap = buildProductSalesMap(orderItems);
        List<ProductSales> salesByProduct = new ArrayList<>(productSalesMap.values());

        // Get top 5 selling products
        List<ProductSales> topProducts = getTopProducts(salesByProduct, 5);

        // Extract data for pie chart
        List<String> productNames = new ArrayList<>();
        List<Double> productRevenues = new ArrayList<>();

        for (ProductSales ps : salesByProduct) {
            productNames.add(ps.getProductName());
            productRevenues.add(ps.getTotalRevenue());
        }

        // Set attributes for JSP
        request.setAttribute("totalSales", totalSales);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalItemsSold", totalItemsSold);
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("salesByProduct", salesByProduct);
        request.setAttribute("topProducts", topProducts);
        request.setAttribute("productNames", productNames);
        request.setAttribute("productRevenues", productRevenues);

        request.getRequestDispatcher("/WEB-INF/views/seller/sales.jsp").forward(request, response);
    }

    // ==================== DATA PROCESSING METHODS ====================

    private double calculateTotalSales(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getSubtotal().doubleValue())
                .sum();
    }

    private int calculateTotalOrders(List<OrderItem> orderItems) {
        Set<Integer> uniqueOrderIds = orderItems.stream()
                .map(OrderItem::getOrderId)
                .collect(Collectors.toSet());
        return uniqueOrderIds.size();
    }

    private int calculateTotalItemsSold(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    private int calculatePendingOrders(List<OrderItem> orderItems) {
        Set<Integer> pendingOrderIds = new HashSet<>();

        for (OrderItem item : orderItems) {
            Order order = orderDao.findById(item.getOrderId());
            if (order != null && "pending".equals(order.getOrderStatus())) {
                pendingOrderIds.add(item.getOrderId());
            }
        }
        return pendingOrderIds.size();
    }

    private Map<Integer, ProductSales> buildProductSalesMap(List<OrderItem> orderItems) {
        Map<Integer, ProductSales> productMap = new HashMap<>();

        for (OrderItem item : orderItems) {
            ProductSales ps = productMap.get(item.getProductId());
            if (ps == null) {
                ps = new ProductSales();
                ps.setProductId(item.getProductId());
                ps.setProductName(item.getProductName());
                ps.setQuantitySold(0);
                ps.setTotalRevenue(0.0);
                productMap.put(item.getProductId(), ps);
            }
            ps.setQuantitySold(ps.getQuantitySold() + item.getQuantity());
            ps.setTotalRevenue(ps.getTotalRevenue() + item.getSubtotal().doubleValue());
        }
        return productMap;
    }

    private List<ProductSales> getTopProducts(List<ProductSales> salesByProduct, int limit) {
        return salesByProduct.stream()
                .sorted((a, b) -> Integer.compare(b.getQuantitySold(), a.getQuantitySold()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ==================== INNER CLASS ====================

    public static class ProductSales {
        private int productId;
        private String productName;
        private int quantitySold;
        private double totalRevenue;

        // Getters and Setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public int getQuantitySold() { return quantitySold; }
        public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    }
}