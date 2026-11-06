package com.hexocnc.lead.controller;

import com.hexocnc.domain.Domain;
import com.hexocnc.lead.model.dto.CreateLeadRequest;
import com.hexocnc.lead.model.dto.LeadResponseDto;
import com.hexocnc.lead.model.dto.UpdateLeadRequest;
import com.hexocnc.lead.service.LeadService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/internal/leads")
@AllArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    public ResponseEntity<LeadResponseDto> createLead(@Valid @RequestBody final CreateLeadRequest request) {
        final var lead = leadService.createNewLead(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lead);
    }

    @GetMapping
    public ResponseEntity<List<LeadResponseDto>> getAllLeads(@RequestParam(required = false) final Domain domain) {
        final var leads = domain == null
                ? leadService.getAllLeads()
                : leadService.getAllLeadsForDomain(domain);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponseDto> getLeadById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLead(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeadResponseDto> updateLeadStatus(@PathVariable Long id,
                                                            @RequestBody UpdateLeadRequest request) {
        return ResponseEntity.ok(leadService.updateLead(id, request));
    }
}
