package com.hexocnc.lead.repository;

import com.hexocnc.domain.Domain;
import com.hexocnc.lead.model.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<LeadEntity, Long> {

    List<LeadEntity> findAllByDomainOrderByCreatedAtDesc(final Domain domain);
}
