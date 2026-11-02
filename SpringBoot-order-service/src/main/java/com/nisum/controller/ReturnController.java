package com.nisum.controller;

import com.nisum.dto.CreateReturnRequest;
import com.nisum.dto.EligibilityRequest;
import com.nisum.dto.EligibilityResponse;
import com.nisum.dto.ReturnResponse;
import com.nisum.service.ReturnService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping("/eligibility")
    public EligibilityResponse checkEligibility(
            @Valid @RequestBody EligibilityRequest request) {
        return returnService.checkEligibility(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnResponse createReturn(
            @Valid @RequestBody CreateReturnRequest request) {
        return returnService.createReturn(request);
    }

    @GetMapping("/{returnId}")
    public ReturnResponse getReturn(@PathVariable Long returnId) {
        return returnService.getReturn(returnId);
    }

    @GetMapping
    public List<ReturnResponse> getReturns(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String customerId) {

        if (orderId != null && !orderId.isBlank()) {
            return returnService.getReturnsByOrder(orderId);
        }
        if (customerId != null && !customerId.isBlank()) {
            return returnService.getReturnsByCustomer(customerId);
        }
        throw new IllegalArgumentException(
                "Provide either orderId or customerId as a query parameter.");
    }

    @PostMapping("/{returnId}/evaluate")
    public ReturnResponse evaluate(@PathVariable Long returnId) {
        return returnService.evaluate(returnId);
    }

    @PostMapping("/{returnId}/cancel")
    public ReturnResponse cancel(@PathVariable Long returnId) {
        return returnService.cancel(returnId);
    }

    @PostMapping("/{returnId}/refund")
    public ReturnResponse recordRefund(@PathVariable Long returnId) {
        return returnService.recordRefund(returnId);
    }
}
