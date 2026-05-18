package com.fiveam.findmycomponent.controller;
import com.fiveam.findmycomponent.dao.OrderDao;
import com.fiveam.findmycomponent.dao.OrderDaoImpl;
import com.fiveam.findmycomponent.dao.OrderItemDao;
import com.fiveam.findmycomponent.dao.OrderItemDaoImpl;
import com.fiveam.findmycomponent.dao.ProductDao;
import com.fiveam.findmycomponent.dao.ProductDaoImpl;
import com.fiveam.findmycomponent.entity.Order;
import com.fiveam.findmycomponent.entity.OrderItem;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    private ProductDao productDao;

    @Override
    public void init() throws ServletException {
        orderDao = new OrderDaoImpl();
        orderItemDao = new OrderItemDaoImpl();
        productDao = new ProductDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String statusFilter = request.getParameter("status");

        if ("view".equals(action)) {
            String orderIdParam = request.getParameter("id");
            if (orderIdParam != null && !orderIdParam.isEmpty()) {
                try {
                    int orderId = Integer.parseInt(orderIdParam);
                    Order order = orderDao.findById(orderId);
                    List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);

                    if (order != null) {
                        request.setAttribute("order", order);
                        request.setAttribute("orderItems", orderItems);
                        request.getRequestDispatcher("/WEB-INF/admin/admin-order-detail.jsp").forward(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    // Invalid ID
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/orders");

        } else {
            List<Order> orders;
            if (statusFilter != null && !statusFilter.isEmpty()) {
                orders = orderDao.findByStatus(statusFilter);
                request.setAttribute("currentStatusFilter", statusFilter);
            } else {
                orders = orderDao.findAll();
            }

            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/admin/admin-orders.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String orderIdParam = request.getParameter("id");

        if (orderIdParam == null || orderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=Invalid order ID");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=Invalid order ID");
            return;
        }

        if ("accept".equals(action)) {
            handleAccept(request, response, orderId);
        } else if ("reject".equals(action)) {
            handleReject(request, response, orderId);
        } else if ("dispatch".equals(action)) {
            handleDispatch(request, response, orderId);
        } else if ("cancel".equals(action)) {
            handleCancel(request, response, orderId);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    private void handleAccept(HttpServletRequest request, HttpServletResponse response, int orderId)
            throws IOException {

        Order order = orderDao.findById(orderId);

        if (order == null || !"pending".equals(order.getOrderStatus())) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=Cannot accept this order");
            return;
        }

        orderDao.markAsAccepted(orderId);
        response.sendRedirect(request.getContextPath() + "/admin/orders?success=Order accepted successfully");
    }

    private void handleReject(HttpServletRequest request, HttpServletResponse response, int orderId)
            throws IOException {

        Order order = orderDao.findById(orderId);

        if (order == null || !"pending".equals(order.getOrderStatus())) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=Cannot reject this order");
            return;
        }

        restoreOrderStock(orderId);
        orderDao.updateStatus(orderId, "rejected");
        response.sendRedirect(request.getContextPath() + "/admin/orders?success=Order rejected successfully");
    }

    private void handleDispatch(HttpServletRequest request, HttpServletResponse response, int orderId)
            throws IOException {

        Order order = orderDao.findById(orderId);

        if (order == null || !"accepted".equals(order.getOrderStatus())) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=Cannot dispatch this order");
            return;
        }

        orderDao.markAsDispatched(orderId);
        response.sendRedirect(request.getContextPath() + "/admin/orders?success=Order dispatched successfully");
    }

    private void handleCancel(HttpServletRequest request, HttpServletResponse response, int orderId)
            throws IOException {

        Order order = orderDao.findById(orderId);

        if (order == null) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=Order not found");
            return;
        }

        String currentStatus = order.getOrderStatus();
        if (!"accepted".equals(currentStatus) && !"dispatched".equals(currentStatus)) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=Cannot cancel this order");
            return;
        }

        restoreOrderStock(orderId);
        orderDao.markAsCancelled(orderId);
        response.sendRedirect(request.getContextPath() + "/admin/orders?success=Order cancelled successfully");
    }

    private void restoreOrderStock(int orderId) {
        List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            productDao.updateStock(item.getProductId(), item.getQuantity());
        }
    }
}