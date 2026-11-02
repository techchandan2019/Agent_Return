
package com.nisum.service;

import com.nisum.dto.CreateReturnRequest;
import com.nisum.dto.EligibilityRequest;
import com.nisum.dto.EligibilityResponse;
import com.nisum.dto.ReturnResponse;
import com.nisum.entity.ReturnCase;
import com.nisum.entity.ReturnStatus;
import com.nisum.entity.ResolutionType;
import com.nisum.exception.BadRequestException;
import com.nisum.exception.ResourceNotFoundException;
import com.nisum.repository.ReturnRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReturnService {

    private final ReturnRepository repository;
    private final EligibilityService eligibilityService;

    public ReturnService(
            ReturnRepository repository,
            EligibilityService eligibilityService) {
        this.repository = repository;
        this.eligibilityService = eligibilityService;
    }

    // 1. Check eligibility without creating a database record
    public EligibilityResponse checkEligibility(
            EligibilityRequest request) {

        return eligibilityService.check(request);
    }

    // 2. Create a return/refund request
    @Transactional
    public ReturnResponse createReturn(CreateReturnRequest request) {

        EligibilityRequest eligibilityRequest =
                new EligibilityRequest(
                        request.orderId(),
                        request.customerId(),
                        request.productId(),
                        request.productName(),
                        request.quantity(),
                        request.itemPrice(),
                        request.reason(),
                        request.deliveryDate(),
                        request.productCategory()
                );

        EligibilityResponse eligibility =
                eligibilityService.check(eligibilityRequest);

        if (!eligibility.eligible()) {
            throw new BadRequestException(eligibility.message());
        }

        ReturnCase entity = new ReturnCase();

        entity.setOrderId(request.orderId());
        entity.setCustomerId(request.customerId());
        entity.setProductId(request.productId());
        entity.setProductName(request.productName());
        entity.setQuantity(request.quantity());
        entity.setItemPrice(request.itemPrice());
        entity.setReason(request.reason());
        entity.setDeliveryDate(request.deliveryDate());

        // New fields
        entity.setProductCategory(request.productCategory());
        entity.setResolutionType(eligibility.resolutionType());
        entity.setReturnRequired(eligibility.returnRequired());

        entity.setStatus(ReturnStatus.REQUESTED);
        entity.setDecisionMessage(eligibility.message());

        return toResponse(repository.save(entity));
    }

    // 3. Get return by ID
    public ReturnResponse getReturn(Long id) {
        return toResponse(findById(id));
    }

    // 4. Get returns by order
    public List<ReturnResponse> getReturnsByOrder(String orderId) {
        return repository.findByOrderId(orderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 5. Get returns by customer
    public List<ReturnResponse> getReturnsByCustomer(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 6. Evaluate the return/refund request
    @Transactional
    public ReturnResponse evaluate(Long id) {

        ReturnCase entity = findById(id);

        if (entity.getStatus() != ReturnStatus.REQUESTED) {
            throw new BadRequestException(
                    "Only REQUESTED cases can be evaluated."
            );
        }

        ResolutionType resolution = entity.getResolutionType();

        if (resolution == ResolutionType.MANUAL_REVIEW) {
            entity.setDecisionMessage(
                    "Request requires manual review."
            );

            return toResponse(repository.save(entity));
        }

        if (resolution == ResolutionType.NONE) {
            entity.setStatus(ReturnStatus.REJECTED);
            entity.setDecisionMessage(
                    "Request is not eligible for a refund or return."
            );

            return toResponse(repository.save(entity));
        }

        entity.setStatus(ReturnStatus.APPROVED);

        if (resolution == ResolutionType.REFUND
                && !entity.isReturnRequired()) {

            entity.setDecisionMessage(
                    "Refund approved. Physical product return is not required."
            );

        } else if (resolution == ResolutionType.REPLACEMENT) {

            entity.setDecisionMessage(
                    "Replacement approved."
            );

        } else {

            entity.setDecisionMessage(
                    "Return and refund approved."
            );
        }

        return toResponse(repository.save(entity));
    }

    // 7. Cancel request
    @Transactional
    public ReturnResponse cancel(Long id) {

        ReturnCase entity = findById(id);

        if (entity.getStatus() == ReturnStatus.REFUNDED
                || entity.getStatus() == ReturnStatus.CANCELLED) {

            throw new BadRequestException(
                    "This request cannot be cancelled."
            );
        }

        entity.setStatus(ReturnStatus.CANCELLED);
        entity.setDecisionMessage("Request cancelled.");

        return toResponse(repository.save(entity));
    }

    // 8. Record refund
    @Transactional
    public ReturnResponse recordRefund(Long id) {

        ReturnCase entity = findById(id);

        if (entity.getStatus() != ReturnStatus.APPROVED) {
            throw new BadRequestException(
                    "Only APPROVED requests can be marked as refunded."
            );
        }

        if (entity.getResolutionType() != ResolutionType.REFUND
                && entity.getResolutionType()
                != ResolutionType.RETURN_AND_REFUND) {

            throw new BadRequestException(
                    "This request does not have a refund resolution."
            );
        }

        entity.setStatus(ReturnStatus.REFUNDED);
        entity.setDecisionMessage(
                "Refund recorded in the learning application."
        );

        return toResponse(repository.save(entity));
    }

    // Find return record
    private ReturnCase findById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Return not found with id: " + id
                ));
    }

    // Convert entity to response
    private ReturnResponse toResponse(ReturnCase entity) {

        return new ReturnResponse(
                entity.getId(),
                entity.getOrderId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getProductName(),
                entity.getQuantity(),
                entity.getItemPrice(),
                entity.getReason(),
                entity.getDeliveryDate(),
                entity.getProductCategory(),
                entity.getResolutionType(),
                entity.isReturnRequired(),
                entity.getStatus(),
                entity.getDecisionMessage(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
