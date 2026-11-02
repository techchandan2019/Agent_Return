package com.nisum.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateReturnRequest(
         String orderId,
         String customerId,
         String productId,
         String productName,
         Integer quantity,
         BigDecimal itemPrice,
         String reason,
         LocalDate deliveryDate,
         ProductCategory productCategory
) {}
