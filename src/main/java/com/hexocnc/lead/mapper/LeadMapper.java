package com.hexocnc.lead.mapper;

import com.hexocnc.event.LeadCreatedEvent;
import com.hexocnc.lead.LeadStatus;
import com.hexocnc.lead.model.LeadEntity;
import com.hexocnc.lead.model.dto.CreateLeadRequest;
import com.hexocnc.lead.model.dto.LeadResponseDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LeadMapper {

    public static LeadResponseDto toDto(final LeadEntity entity) {
        return new LeadResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDomain(),
                entity.getPhone(),
                entity.getDescription(),
                entity.getComment(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static LeadEntity toEntity(final CreateLeadRequest request) {
        final var entity = new LeadEntity();
        entity.setDomain(request.getDomain());
        entity.setName(request.getName());
        entity.setPhone(request.getPhone());
        entity.setDescription(request.getDescription());
        entity.setStatus(LeadStatus.NEW);
        return entity;
    }

    public static LeadEntity toEntity(final LeadCreatedEvent event) {
        final var entity = new LeadEntity();
        entity.setDomain(event.domain());
        entity.setName(event.name());
        entity.setPhone(event.phone());
        entity.setDescription(event.description());
        entity.setStatus(LeadStatus.NEW);
        entity.setCreatedAt(event.createdAt());
        return entity;
    }
}
