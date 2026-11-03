package com.hexocnc.lead.event;

import com.hexocnc.domain.Domain;

import java.time.LocalDateTime;

/**
 * Published by {@code LeadService} when a new lead is saved. Carries a snapshot of the fields
 * listeners need, so they don't touch the (possibly detached) entity.
 */
public record LeadCreatedEvent(
        Long id,
        Domain domain,
        String name,
        String phone,
        String comment,
        LocalDateTime createdAt) {
}
