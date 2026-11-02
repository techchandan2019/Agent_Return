package com.nisum.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String customerId,
        String productId,
        String product,
        Integer quantity,
        BigDecimal itemPrice,
        ProductCategory productCategory,
        String status,
        LocalDate deliveryDate,
        LocalDateTime createdAt
) {}