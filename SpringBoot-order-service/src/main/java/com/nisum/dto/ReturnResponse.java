package com.nisum.dto;

import com.nisum.entity.ProductCategory;
import com.nisum.entity.ResolutionType;
import com.nisum.entity.ReturnStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReturnResponse(
        Long id,
        String orderId,
        String customerId,
        String productId,
        String productName,
        Integer quantity,
        BigDecimal itemPrice,
        String reason,
        LocalDate deliveryDate,
        ProductCategory productCategory,
        ResolutionType resolutionType,
        boolean returnRequired,
        ReturnStatus status,
        String decisionMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
