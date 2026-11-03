package com.hexocnc.lead.service;

import com.hexocnc.domain.Domain;
import com.hexocnc.lead.exception.LeadNotFoundException;
import com.hexocnc.lead.mapper.LeadMapper;
import com.hexocnc.lead.model.LeadEntity;
import com.hexocnc.lead.model.dto.CreateLeadRequest;
import com.hexocnc.lead.model.dto.LeadResponseDto;
import com.hexocnc.lead.model.dto.UpdateLeadRequest;
import com.hexocnc.lead.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;

    @Transactional
    public LeadResponseDto createNewLead(final CreateLeadRequest request) {
        final var leadEntity = LeadMapper.toEntity(request);
        leadRepository.save(leadEntity);
        return LeadMapper.toDto(leadEntity);

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
