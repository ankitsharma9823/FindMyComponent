package com.fiveam.findmycomponent.controller;
import com.fiveam.findmycomponent.dao.*;
import com.fiveam.findmycomponent.entity.*;
import com.fiveam.findmycomponent.utils.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/buyer/orders")
public class BuyerOrderServlet extends HttpServlet {

    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    private CartDao cartDao;
    private ProductDao productDao;

    private static int orderCounter = initializeCounter();

    /**
     * Initialize the order counter from the highest existing order number in database
     * This runs ONCE when the servlet class is loaded
     */
    private static int initializeCounter() {
        String sql = """
            SELECT MAX(CAST(SUBSTRING_INDEX(order_number, '-', -1) AS UNSIGNED))
            FROM orders
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int max = rs.getInt(1); // returns 0 if table is empty
                return Math.max(max, 1000); // start from at least 1000
            }

        } catch (SQLException e) {
            System.err.println("Error initializing order counter: " + e.getMessage());
            e.printStackTrace();
        }
        return 1000; // fallback default
    }

    @Override
    public void init() throws ServletException {
        orderDao = new OrderDaoImpl();
        orderItemDao = new OrderItemDaoImpl();
        cartDao = new CartDaoImpl();
        productDao = new ProductDaoImpl();
    }

    // ==================== HTTP METHODS ====================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User buyer = (User) session.getAttribute("user");

        String action = request.getParameter("action");

        if ("view".equals(action)) {
            String orderIdParam = request.getParameter("id");
            if (orderIdParam != null && !orderIdParam.isEmpty()) {
                try {
                    int orderId = Integer.parseInt(orderIdParam);
                    Order order = orderDao.findById(orderId);

                    if (order != null && order.getUserId() == buyer.getId()) {
                        List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);
                        request.setAttribute("order", order);
                        request.setAttribute("orderItems", orderItems);
                        request.getRequestDispatcher("/WEB-INF/views/shop/order-detail.jsp").forward(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    // Invalid ID
                }
            }
            response.sendRedirect(request.getContextPath() + "/buyer/orders");

        } else {
            List<Order> orders = orderDao.findByUserId(buyer.getId());
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/shop/orders.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User buyer = (User) session.getAttribute("user");

        String action = request.getParameter("action");

        if ("place".equals(action)) {
            handlePlaceOrder(request, response, buyer);
        } else if ("cancel".equals(action)) {
            handleCancelOrder(request, response, buyer);
        } else {
            response.sendRedirect(request.getContextPath() + "/buyer/orders");
        }
    }

    // ==================== ACTION HANDLERS ====================

    private void handlePlaceOrder(HttpServletRequest request, HttpServletResponse response, User buyer)
            throws IOException, ServletException {

        List<CartItem> cartItems = cartDao.getCartByUserId(buyer.getId());

        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Cart is empty");
            return;
        }

        // Validate stock
        for (CartItem cartItem : cartItems) {
            Product product = productDao.findById(cartItem.getProductId());
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Insufficient stock for: "
                        + cartItem.getProductName());
                return;
            }
        }

        // Get shipping address
        String shippingAddress = request.getParameter("shippingAddress");
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            shippingAddress = "Address not provided";
        }

        // Calculate total
        double totalAmount = cartDao.getCartTotal(buyer.getId());

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(buyer.getId());
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));
        order.setOrderStatus("pending");
        order.setPaymentStatus("pending");
        order.setPaymentMethod("COD");
        order.setShippingAddress(shippingAddress);

        orderDao.save(order);

        // Create order items and reduce stock
        for (CartItem cartItem : cartItems) {
            Product product = productDao.findById(cartItem.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setSellerId(product.getSellerId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPrice(BigDecimal.valueOf(cartItem.getProductPrice()));
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(BigDecimal.valueOf(cartItem.getTotalPrice()));

            orderItemDao.save(orderItem);
            productDao.updateStock(cartItem.getProductId(), -cartItem.getQuantity());
        }

        // Clear cart
        cartDao.clearCart(buyer.getId());

        // Store order info for confirmation
        request.getSession().setAttribute("lastOrderId", order.getId());
        request.getSession().setAttribute("lastOrderNumber", order.getOrderNumber());
        request.getSession().setAttribute("lastOrderTotal", totalAmount);

        response.sendRedirect(request.getContextPath() + "/buyer/orders?action=confirmation");
    }

    private void handleCancelOrder(HttpServletRequest request, HttpServletResponse response, User buyer)
            throws IOException {

        String orderIdParam = request.getParameter("id");

        if (orderIdParam == null || orderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/buyer/orders?error=Invalid order");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdParam);
            Order order = orderDao.findById(orderId);

            if (order != null && order.getUserId() == buyer.getId() && "pending".equals(order.getOrderStatus())) {

                // Restore stock
                List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);
                for (OrderItem item : orderItems) {
                    productDao.updateStock(item.getProductId(), item.getQuantity());
                }

                orderDao.markAsCancelled(orderId);
                response.sendRedirect(request.getContextPath() + "/buyer/orders?success=Order cancelled successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/buyer/orders?error=Cannot cancel this order");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/buyer/orders?error=Invalid order ID");
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Generates unique order number using the static counter
     * Thread-safe using synchronized
     */
    private synchronized String generateOrderNumber() {
        orderCounter++;
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ORD-" + datePrefix + "-" + orderCounter;
    }
}