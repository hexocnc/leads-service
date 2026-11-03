package com.hexocnc.lead.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_events")
@Getter
@Setter
public class ProcessedEventEntity {

    @Id
    @Column(name = "event_key", length = 100)
    private String eventKey;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
}
