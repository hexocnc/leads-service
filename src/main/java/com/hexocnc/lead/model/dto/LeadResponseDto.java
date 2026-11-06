package com.hexocnc.lead.model.dto;

import com.hexocnc.domain.Domain;
import com.hexocnc.lead.LeadStatus;

import java.time.LocalDateTime;

public record LeadResponseDto(
        Long id,
        String name,
        Domain domain,
        String phone,
        String description,
        String comment,
        LeadStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
