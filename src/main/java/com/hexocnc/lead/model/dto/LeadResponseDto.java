package com.hexocnc.lead.model.dto;

import com.hexocnc.domain.Domain;
import com.hexocnc.lead.LeadStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LeadResponseDto {

    private String name;

    private Domain domain;

    private String phone;

    private String description;

    private String comment;

    private LeadStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
