
package com.nisum.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal itemPrice;

    @Column(nullable = false)
    private String productCategory;

    @Column(nullable = false)
    private String status;

    private LocalDate deliveryDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected OrderEntity() {}

    public OrderEntity(
            String customerId,
            String productId,
            String product,
            int quantity,
            BigDecimal itemPrice,
            String productCategory,
            LocalDate deliveryDate) {

        this.customerId = customerId;
        this.productId = productId;
        this.product = product;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
        this.productCategory = productCategory;
        this.deliveryDate = deliveryDate;
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getCustomerId() { return customerId; }

    public String getProductId() { return productId; }

    public String getProduct() { return product; }

    public int getQuantity() { return quantity; }

    public BigDecimal getItemPrice() { return itemPrice; }

    public String getProductCategory() { return productCategory; }

    public String getStatus() { return status; }

    public LocalDate getDeliveryDate() { return deliveryDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
