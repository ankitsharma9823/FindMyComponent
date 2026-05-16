package com.fiveam.findmycomponent.entity;


import java.math.BigDecimal;
import java.util.Date;

/**
 * OrderItem entity - Represents a product within an order
 * Maps to 'order_items' table in database
 * Each order item belongs to one seller
 */
public class OrderItem {

    // Fields matching database columns
    private int id;
    private int orderId;
    private int productId;
    private int sellerId;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;
    private BigDecimal subtotal;
    private String sellerStatus;
    private Date sellerRespondedAt;

    // Default constructor
    public OrderItem() {}

    // Constructor for creating order item from cart
    public OrderItem(int orderId, int productId, int sellerId, String productName,
                     BigDecimal productPrice, int quantity, BigDecimal subtotal) {
        this.orderId = orderId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.sellerStatus = "pending";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getSellerStatus() {
        return sellerStatus;
    }

    public void setSellerStatus(String sellerStatus) {
        this.sellerStatus = sellerStatus;
    }

    public Date getSellerRespondedAt() {
        return sellerRespondedAt;
    }

    public void setSellerRespondedAt(Date sellerRespondedAt) {
        this.sellerRespondedAt = sellerRespondedAt;
    }

    // Helper methods
    public boolean isPending() {
        return "pending".equals(sellerStatus);
    }

    public boolean isAccepted() {
        return "accepted".equals(sellerStatus);
    }

    public boolean isRejected() {
        return "rejected".equals(sellerStatus);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", sellerId=" + sellerId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", sellerStatus='" + sellerStatus + '\'' +
                '}';
    }
}