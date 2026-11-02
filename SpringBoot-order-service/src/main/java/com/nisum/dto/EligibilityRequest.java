package com.nisum.dto;

import com.nisum.entity.ProductCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EligibilityRequest(
        @NotBlank String orderId,
        @NotBlank String customerId,
        @NotBlank String productId,
        @NotBlank String productName,
        @NotNull @Min(1) Integer quantity,
        @NotNull @DecimalMin("0.01") BigDecimal itemPrice,
        @NotBlank String reason,
        @NotNull LocalDate deliveryDate,
        @NotNull ProductCategory productCategory
) {}
