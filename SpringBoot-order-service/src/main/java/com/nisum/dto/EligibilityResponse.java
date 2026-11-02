package com.nisum.dto;

import com.nisum.entity.ResolutionType;

public record EligibilityResponse(
        boolean eligible,
        String message,
        ResolutionType resolutionType,
        boolean returnRequired
) {}
