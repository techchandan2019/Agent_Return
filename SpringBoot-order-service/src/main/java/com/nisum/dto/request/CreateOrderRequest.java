package com.nisum.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOrderRequest(
        String customerId,
        String productId,
        String product,
        Integer quantity,
        BigDecimal itemPrice,
        String productCategory,
        LocalDate deliveryDate
) {}