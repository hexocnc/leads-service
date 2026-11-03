package com.hexocnc.lead.service;

import com.hexocnc.lead.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/** Removes processed-event markers past the retention window so the table doesn't grow forever. */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessedEventsCleanupJob {

    private final ProcessedEventRepository processedEventRepository;

    @Value("${app.processed-events.retention:1d}")
    private Duration retention;

    @Transactional
    @Scheduled(cron = "${app.processed-events.cleanup-cron:0 0 3 * * *}")
    public void cleanup() {
        final int deleted = processedEventRepository.deleteOlderThan(LocalDateTime.now().minus(retention));
        log.info("Deleted {} processed events older than {}", deleted, retention);
    }
}
