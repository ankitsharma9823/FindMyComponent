package com.fiveam.findmycomponent.dao;

import com.fiveam.findmycomponent.entity.OrderItem;
import com.fiveam.findmycomponent.utils.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderItemDaoImpl - Implementation of OrderItemDao interface
 * Handles all database operations for OrderItem entity
 */
public class OrderItemDaoImpl implements OrderItemDao {

    // ========== CREATE ==========

    @Override
    public boolean save(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id, seller_id, product_name, product_price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, orderItem.getOrderId());
            pstmt.setInt(2, orderItem.getProductId());
            pstmt.setInt(3, orderItem.getSellerId());
            pstmt.setString(4, orderItem.getProductName());
            pstmt.setBigDecimal(5, orderItem.getProductPrice());
            pstmt.setInt(6, orderItem.getQuantity());
            pstmt.setBigDecimal(7, orderItem.getSubtotal());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    orderItem.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error saving order item: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while saving order item", e);
        }
        return false;
    }

    @Override
    public boolean saveAll(List<OrderItem> orderItems) {
        String sql = "INSERT INTO order_items (order_id, product_id, seller_id, product_name, product_price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (OrderItem item : orderItems) {
                pstmt.setInt(1, item.getOrderId());
                pstmt.setInt(2, item.getProductId());
                pstmt.setInt(3, item.getSellerId());
                pstmt.setString(4, item.getProductName());
                pstmt.setBigDecimal(5, item.getProductPrice());
                pstmt.setInt(6, item.getQuantity());
                pstmt.setBigDecimal(7, item.getSubtotal());
                pstmt.addBatch();
            }

            int[] affectedRows = pstmt.executeBatch();
            return affectedRows.length == orderItems.size();

        } catch (SQLException e) {
            System.err.println("Error saving order items batch: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while saving order items batch", e);
        }
    }

    // ========== READ ==========

    @Override
    public OrderItem findById(int id) {
        String sql = "SELECT * FROM order_items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrderItem(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding order item by ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order item with id: " + id, e);
        }
        return null;
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding order items by order ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order items for order: " + orderId, e);
        }
        return items;
    }

    @Override
    public List<OrderItem> findBySellerId(int sellerId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE seller_id = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding order items by seller ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order items for seller: " + sellerId, e);
        }
        return items;
    }

    @Override
    public List<OrderItem> findByProductId(int productId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding order items by product ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order items for product: " + productId, e);
        }
        return items;
    }

    @Override
    public List<OrderItem> findAll() {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all order items: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching all order items", e);
        }
        return items;
    }

    // ========== UPDATE ==========

    @Override
    public boolean update(OrderItem orderItem) {
        // Note: Most fields should not be updated after order is placed
        // Only quantity might be updated in rare cases
        String sql = "UPDATE order_items SET quantity = ?, subtotal = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderItem.getQuantity());
            pstmt.setBigDecimal(2, orderItem.getSubtotal());
            pstmt.setInt(3, orderItem.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating order item: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while updating order item with id: " + orderItem.getId(), e);
        }
    }

    // ========== DELETE ==========

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting order item: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting order item with id: " + id, e);
        }
    }

    @Override
    public boolean deleteByOrderId(int orderId) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting order items by order ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting order items for order: " + orderId, e);
        }
    }

    // ========== QUERIES ==========

    @Override
    public double getSubtotalByOrderId(int orderId) {
        String sql = "SELECT SUM(subtotal) FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting subtotal by order ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while getting subtotal for order: " + orderId, e);
        }
        return 0.0;
    }

    @Override
    public int getItemCountByOrderId(int orderId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting item count by order ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while getting item count for order: " + orderId, e);
        }
        return 0;
    }

    // ========== COUNT ==========

    @Override
    public int getCountByOrderId(int orderId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting count by order ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while getting count for order: " + orderId, e);
        }
        return 0;
    }

    @Override
    public int getCountBySellerId(int sellerId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE seller_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting count by seller ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while getting count for seller: " + sellerId, e);
        }
        return 0;
    }

    // ========== PRIVATE HELPER ==========

    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getInt("id"));
        item.setOrderId(rs.getInt("order_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setSellerId(rs.getInt("seller_id"));
        item.setProductName(rs.getString("product_name"));
        item.setProductPrice(rs.getBigDecimal("product_price"));
        item.setQuantity(rs.getInt("quantity"));
        item.setSubtotal(rs.getBigDecimal("subtotal"));
        return item;
    }
}