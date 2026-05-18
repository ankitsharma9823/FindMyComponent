package com.fiveam.findmycomponent.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * OrderItem entity - Represents a product within an order
 * Maps to 'order_items' table in database
 *
 * Note: product_name and product_price are snapshots at order time.
 * They DO NOT change even if the original product is updated later.
 */
public class OrderItem {

    // Fields matching database columns
    private int id;
    private int orderId;
    private int productId;
    private int sellerId;
    private String productName;      // Snapshot - not a foreign key
    private BigDecimal productPrice;  // Snapshot - not a foreign key
    private int quantity;
    private BigDecimal subtotal;

    // Optional - for display purposes only (not stored in DB)
    private String sellerName;
    private String productBrand;
    private String productImageUrl;

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

    // Optional display fields (not stored in DB)
    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    // Helper methods
    public double getTotalPrice() {
        return productPrice.doubleValue() * quantity;
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
                ", subtotal=" + subtotal +
                '}';
    }
}