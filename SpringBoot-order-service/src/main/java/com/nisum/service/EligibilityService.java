package com.nisum.service;

import com.nisum.dto.EligibilityRequest;
import com.nisum.dto.EligibilityResponse;
import com.nisum.entity.ProductCategory;
import com.nisum.entity.ResolutionType;
import com.nisum.entity.ReturnCase;
import com.nisum.repository.ReturnRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class EligibilityService {

    private final ReturnRepository returnRepository;

    public EligibilityService(ReturnRepository returnRepository) {
        this.returnRepository = returnRepository;
    }

    public ReturnCase evaluate(ReturnCase request) {

        ReturnCase result = new ReturnCase();

        result.setOrderId(request.getOrderId());
        result.setCustomerId(request.getCustomerId());
        result.setProductId(request.getProductId());
        result.setProductName(request.getProductName());
        result.setQuantity(request.getQuantity());
        result.setItemPrice(request.getItemPrice());
        result.setReason(request.getReason());
        result.setDeliveryDate(request.getDeliveryDate());
        result.setProductCategory(request.getProductCategory());

        // Rule 1: Grocery damage or expiry
        if (request.getProductCategory() == ProductCategory.GROCERY
                && ("DAMAGED".equalsIgnoreCase(request.getReason())
                || "EXPIRED".equalsIgnoreCase(request.getReason())
                || "MISSING".equalsIgnoreCase(request.getReason()))) {

            result.setResolutionType(ResolutionType.REFUND);
            result.setReturnRequired(false);
            result.setDecisionMessage(
                    "Eligible for refund. Physical return is not required."
            );

            return result;
        }

        // Rule 2: Normal return window
        long days = ChronoUnit.DAYS.between(
                request.getDeliveryDate(),
                LocalDate.now()
        );

        if (days < 0) {
            result.setResolutionType(ResolutionType.NONE);
            result.setReturnRequired(false);
            result.setDecisionMessage("Delivery date cannot be in the future.");
            return result;
        }

        if (days <= 30) {
            result.setResolutionType(ResolutionType.RETURN_AND_REFUND);
            result.setReturnRequired(true);
            result.setDecisionMessage(
                    "Eligible under the 30-day return policy."
            );
        } else {
            result.setResolutionType(ResolutionType.NONE);
            result.setReturnRequired(false);
            result.setDecisionMessage("Return window has expired.");
        }

        return result;
    }
    public EligibilityResponse check(EligibilityRequest request) {

        // Rule 1: Validate delivery date
        long days = ChronoUnit.DAYS.between(
                request.deliveryDate(),
                LocalDate.now()
        );

        if (days < 0) {
            return new EligibilityResponse(
                    false,
                    "Delivery date cannot be in the future.",
                    ResolutionType.NONE,
                    false
            );
        }

        // Rule 2: Damaged, expired or missing grocery
        if (request.productCategory() == ProductCategory.GROCERY
                && (
                "DAMAGED".equalsIgnoreCase(request.reason())
                        || "EXPIRED".equalsIgnoreCase(request.reason())
                        || "MISSING".equalsIgnoreCase(request.reason())
        )) {

            return new EligibilityResponse(
                    true,
                    "Eligible for refund. Physical return is not required.",
                    ResolutionType.REFUND,
                    false
            );
        }

        // Rule 3: Normal 30-day return policy
        if (days <= 30) {

            return new EligibilityResponse(
                    true,
                    "Eligible under the 30-day return policy.",
                    ResolutionType.RETURN_AND_REFUND,
                    true
            );
        }

        // Rule 4: Return window expired
        return new EligibilityResponse(
                false,
                "Return window has expired.",
                ResolutionType.NONE,
                false
        );
    }
}