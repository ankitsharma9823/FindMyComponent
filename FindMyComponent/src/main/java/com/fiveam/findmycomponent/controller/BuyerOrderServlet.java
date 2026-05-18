package com.fiveam.findmycomponent.controller;
import com.fiveam.findmycomponent.dao.CartDao;
import com.fiveam.findmycomponent.dao.CartDaoImpl;
import com.fiveam.findmycomponent.dao.OrderDao;
import com.fiveam.findmycomponent.dao.OrderDaoImpl;
import com.fiveam.findmycomponent.dao.OrderItemDao;
import com.fiveam.findmycomponent.dao.OrderItemDaoImpl;
import com.fiveam.findmycomponent.dao.ProductDao;
import com.fiveam.findmycomponent.dao.ProductDaoImpl;
import com.fiveam.findmycomponent.entity.CartItem;
import com.fiveam.findmycomponent.entity.Order;
import com.fiveam.findmycomponent.entity.OrderItem;
import com.fiveam.findmycomponent.entity.User;
import com.fiveam.findmycomponent.entity.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * BuyerOrderServlet - Handles order placement and order history for buyers
 * URL: /buyer/orders
 *
 * GET requests:
 * - /buyer/orders - Display order history for logged-in buyer
 *
 * POST requests:
 * - action=place - Create new order from cart
 * - action=cancel - Cancel pending order
 */
@WebServlet("/buyer/orders")
public class BuyerOrderServlet extends HttpServlet {

    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    private CartDao cartDao;
    private ProductDao productDao;

    private static int orderCounter = 1000;

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
            // View specific order details
            String orderIdParam = request.getParameter("id");
            if (orderIdParam != null && !orderIdParam.isEmpty()) {
                try {
                    int orderId = Integer.parseInt(orderIdParam);
                    Order order = orderDao.findById(orderId);

                    // Verify order belongs to this buyer
                    if (order != null && order.getUserId() == buyer.getId()) {
                        List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);
                        request.setAttribute("order", order);
                        request.setAttribute("orderItems", orderItems);
                        request.getRequestDispatcher("/WEB-INF/buyer/order-detail.jsp").forward(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    // Invalid ID, fall through to order list
                }
            }
            response.sendRedirect(request.getContextPath() + "/buyer/orders");

        } else {
            // Default: Show order history
            List<Order> orders = orderDao.findByUserId(buyer.getId());
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/buyer/orders.jsp").forward(request, response);
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

    /**
     * Handles order placement from cart
     */
    private void handlePlaceOrder(HttpServletRequest request, HttpServletResponse response, User buyer)
            throws IOException, ServletException {

        // Get cart items
        List<CartItem> cartItems = cartDao.getCartByUserId(buyer.getId());

        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Cart is empty");
            return;
        }

        // Validate stock before placing order
        for (CartItem cartItem : cartItems) {
            int availableStock = productDao.findById(cartItem.getProductId()).getStockQuantity();
            if (availableStock < cartItem.getQuantity()) {
                response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Insufficient stock for: "
                        + cartItem.getProductName());
                return;
            }
        }

        // Get shipping address from request or use default
        String shippingAddress = request.getParameter("shippingAddress");
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            shippingAddress = "Address not provided";
        }

        // Calculate total amount
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
//            orderItem.setSellerId(cartItem.getSellerId());
            orderItem.setSellerId(product.getSellerId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPrice(BigDecimal.valueOf(cartItem.getProductPrice()));
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(BigDecimal.valueOf(cartItem.getTotalPrice()));

            orderItemDao.save(orderItem);

            // Reduce product stock
            productDao.updateStock(cartItem.getProductId(), -cartItem.getQuantity());
        }

        // Clear the cart
        cartDao.clearCart(buyer.getId());

        // Store order info in session for confirmation page
        request.getSession().setAttribute("lastOrderId", order.getId());
        request.getSession().setAttribute("lastOrderNumber", order.getOrderNumber());
        request.getSession().setAttribute("lastOrderTotal", totalAmount);

        // Redirect to order confirmation page
        response.sendRedirect(request.getContextPath() + "/buyer/order-confirmation.jsp");
    }

    /**
     * Handles cancellation of pending order
     */
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

            // Verify order belongs to buyer and is pending
            if (order != null && order.getUserId() == buyer.getId() && "pending".equals(order.getOrderStatus())) {

                // Restore stock for all items in this order
                List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);
                for (OrderItem item : orderItems) {
                    productDao.updateStock(item.getProductId(), item.getQuantity());
                }

                // Update order status to cancelled
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
     * Generates unique order number (e.g., ORD-1001)
     */
    private synchronized String generateOrderNumber() {
        orderCounter++;
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ORD-" + datePrefix + "-" + orderCounter;
    }
}