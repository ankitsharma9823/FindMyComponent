package com.fiveam.findmycomponent.dao;

import com.fiveam.findmycomponent.entity.OrderItem;
import java.util.List;

/**
 * OrderItemDao Interface - Defines database operations for OrderItem entity
 */
public interface OrderItemDao {

    // ========== CREATE ==========
    boolean save(OrderItem orderItem);
    boolean saveAll(List<OrderItem> orderItems);

    // ========== READ ==========
    OrderItem findById(int id);
    List<OrderItem> findByOrderId(int orderId);
    List<OrderItem> findBySellerId(int sellerId);
    List<OrderItem> findByProductId(int productId);
    List<OrderItem> findBySellerStatus(int orderId, String sellerStatus);
    List<OrderItem> findAll();

    // ========== UPDATE ==========
    boolean update(OrderItem orderItem);
    boolean updateSellerStatus(int orderItemId, String sellerStatus);
    boolean updateSellerStatusForOrderItem(int orderId, int productId, int sellerId, String sellerStatus);

    // ========== DELETE ==========
    boolean delete(int id);
    boolean deleteByOrderId(int orderId);

    // ========== QUERIES ==========
    double getAcceptedSubtotal(int orderId);
    boolean hasAllSellersResponded(int orderId);
    boolean hasAnyRejected(int orderId);
    int getPendingCountByOrderId(int orderId);

    // ========== COUNT ==========
    int getCountByOrderId(int orderId);
    int getCountBySellerId(int sellerId);
}