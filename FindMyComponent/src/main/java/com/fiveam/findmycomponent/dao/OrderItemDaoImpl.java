package com.fiveam.findmycomponent.dao;
import com.fiveam.findmycomponent.entity.OrderItem;
import com.fiveam.findmycomponent.utils.DatabaseConnection;
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
        String sql = "INSERT INTO order_items (order_id, product_id, seller_id, product_name, product_price, quantity, subtotal, seller_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, orderItem.getOrderId());
            pstmt.setInt(2, orderItem.getProductId());
            pstmt.setInt(3, orderItem.getSellerId());
            pstmt.setString(4, orderItem.getProductName());
            pstmt.setBigDecimal(5, orderItem.getProductPrice());
            pstmt.setInt(6, orderItem.getQuantity());
            pstmt.setBigDecimal(7, orderItem.getSubtotal());
            pstmt.setString(8, orderItem.getSellerStatus());

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
        String sql = "INSERT INTO order_items (order_id, product_id, seller_id, product_name, product_price, quantity, subtotal, seller_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (OrderItem orderItem : orderItems) {
                pstmt.setInt(1, orderItem.getOrderId());
                pstmt.setInt(2, orderItem.getProductId());
                pstmt.setInt(3, orderItem.getSellerId());
                pstmt.setString(4, orderItem.getProductName());
                pstmt.setBigDecimal(5, orderItem.getProductPrice());
                pstmt.setInt(6, orderItem.getQuantity());
                pstmt.setBigDecimal(7, orderItem.getSubtotal());
                pstmt.setString(8, orderItem.getSellerStatus());
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
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding order items by order ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order items for order: " + orderId, e);
        }
        return orderItems;
    }

    @Override
    public List<OrderItem> findBySellerId(int sellerId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE seller_id = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding order items by seller ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order items for seller: " + sellerId, e);
        }
        return orderItems;
    }

    @Override
    public List<OrderItem> findByProductId(int productId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding order items by product ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order items for product: " + productId, e);
        }
        return orderItems;
    }

    @Override
    public List<OrderItem> findBySellerStatus(int orderId, String sellerStatus) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ? AND seller_status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setString(2, sellerStatus);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding order items by status: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order items with status: " + sellerStatus, e);
        }
        return orderItems;
    }

    @Override
    public List<OrderItem> findAll() {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM order_items ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all order items: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching all order items", e);
        }
        return orderItems;
    }

    // ========== UPDATE ==========

    @Override
    public boolean update(OrderItem orderItem) {
        String sql = "UPDATE order_items SET product_name = ?, product_price = ?, quantity = ?, subtotal = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderItem.getProductName());
            pstmt.setBigDecimal(2, orderItem.getProductPrice());
            pstmt.setInt(3, orderItem.getQuantity());
            pstmt.setBigDecimal(4, orderItem.getSubtotal());
            pstmt.setInt(5, orderItem.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating order item: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while updating order item with id: " + orderItem.getId(), e);
        }
    }

    @Override
    public boolean updateSellerStatus(int orderItemId, String sellerStatus) {
        String sql = "UPDATE order_items SET seller_status = ?, seller_responded_at = NOW() WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sellerStatus);
            pstmt.setInt(2, orderItemId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating seller status: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while updating seller status for order item: " + orderItemId, e);
        }
    }

    @Override
    public boolean updateSellerStatusForOrderItem(int orderId, int productId, int sellerId, String sellerStatus) {
        String sql = "UPDATE order_items SET seller_status = ?, seller_responded_at = NOW() WHERE order_id = ? AND product_id = ? AND seller_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sellerStatus);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, productId);
            pstmt.setInt(4, sellerId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating seller status for order item: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while updating seller status", e);
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
    public double getAcceptedSubtotal(int orderId) {
        String sql = "SELECT SUM(subtotal) FROM order_items WHERE order_id = ? AND seller_status = 'accepted'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting accepted subtotal: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while getting accepted subtotal for order: " + orderId, e);
        }
        return 0.0;
    }

    @Override
    public boolean hasAllSellersResponded(int orderId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE order_id = ? AND seller_status = 'pending'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking seller responses: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while checking seller responses for order: " + orderId, e);
        }
        return false;
    }

    @Override
    public boolean hasAnyRejected(int orderId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE order_id = ? AND seller_status = 'rejected'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking rejected items: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while checking rejected items for order: " + orderId, e);
        }
        return false;
    }

    @Override
    public int getPendingCountByOrderId(int orderId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE order_id = ? AND seller_status = 'pending'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting pending count: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while getting pending count for order: " + orderId, e);
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
            System.err.println("Error getting order item count: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while getting order item count for order: " + orderId, e);
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
            System.err.println("Error getting order item count by seller: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while getting order item count for seller: " + sellerId, e);
        }
        return 0;
    }

    // ========== PRIVATE HELPER ==========

    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(rs.getInt("id"));
        orderItem.setOrderId(rs.getInt("order_id"));
        orderItem.setProductId(rs.getInt("product_id"));
        orderItem.setSellerId(rs.getInt("seller_id"));
        orderItem.setProductName(rs.getString("product_name"));
        orderItem.setProductPrice(rs.getBigDecimal("product_price"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setSubtotal(rs.getBigDecimal("subtotal"));
        orderItem.setSellerStatus(rs.getString("seller_status"));
        orderItem.setSellerRespondedAt(rs.getTimestamp("seller_responded_at"));
        return orderItem;
    }
}