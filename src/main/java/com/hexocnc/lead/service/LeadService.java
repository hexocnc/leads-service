package com.hexocnc.lead.service;

import com.hexocnc.domain.Domain;
import com.hexocnc.event.LeadCreatedEvent;
import com.hexocnc.lead.exception.LeadNotFoundException;
import com.hexocnc.lead.mapper.LeadMapper;
import com.hexocnc.lead.model.LeadEntity;
import com.hexocnc.lead.model.dto.CreateLeadRequest;
import com.hexocnc.lead.model.dto.LeadResponseDto;
import com.hexocnc.lead.model.dto.UpdateLeadRequest;
import com.hexocnc.lead.repository.LeadRepository;
import com.hexocnc.lead.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public LeadResponseDto createNewLead(final CreateLeadRequest request) {
        final var leadEntity = LeadMapper.toEntity(request);
        leadRepository.save(leadEntity);
        return LeadMapper.toDto(leadEntity);

    }

    @Transactional
    public void createLeadFromEvent(final LeadCreatedEvent event) {
        // Same transaction as the save: either both the marker and the lead are stored, or neither.
        final var eventKey = "%s:%s".formatted(event.domain(), event.id());
        if (processedEventRepository.insertIfAbsent(eventKey, LocalDateTime.now()) == 0) {
            log.info("Skipping duplicate LeadCreatedEvent {}", eventKey);
            return;
        }
        final var leadEntity = LeadMapper.toEntity(event);
        leadRepository.save(leadEntity);
    }

    @Transactional
    public LeadResponseDto updateLead(final Long leadId,
                                      final UpdateLeadRequest request) {
        final LeadEntity leadEntity = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead with id %s not found".formatted(leadId)));
        leadEntity.setStatus(request.newStatus());
        leadEntity.setComment(request.comment());
        leadRepository.save(leadEntity);
        return LeadMapper.toDto(leadEntity);
    }

    @Transactional(readOnly = true)
    public List<LeadResponseDto> getAllLeads() {
        return leadRepository.findAll().stream()
                .map(LeadMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeadResponseDto> getAllLeadsForDomain(final Domain domain) {
        return leadRepository.findAllByDomainOrderByCreatedAtDesc(domain).stream()
                .map(LeadMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public LeadResponseDto getLead(final Long id) {
        return leadRepository.findById(id).
                map(LeadMapper::toDto)
                .orElseThrow(() -> new LeadNotFoundException("Lead with id %s not found".formatted(id)));
    }
}
