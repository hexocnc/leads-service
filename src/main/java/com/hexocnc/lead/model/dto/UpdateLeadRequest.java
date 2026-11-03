package com.hexocnc.lead.model.dto;

import com.hexocnc.lead.LeadStatus;

public record UpdateLeadRequest(LeadStatus newStatus, String comment) {
}
