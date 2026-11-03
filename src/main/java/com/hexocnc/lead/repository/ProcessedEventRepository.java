package com.hexocnc.lead.repository;

import com.hexocnc.lead.model.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, String> {

    /**
     * Atomically records the event as processed. A plain {@code save()} can't be used here: with an
     * assigned id it does select-then-insert, so two concurrent duplicates would race and the loser
     * would fail with a constraint violation that marks the transaction rollback-only.
     *
     * @return 1 if the event was recorded now, 0 if it had already been processed
     */
    @Modifying
    @Query(value = """
            INSERT INTO processed_events (event_key, processed_at)
            VALUES (:eventKey, :processedAt)
            ON CONFLICT (event_key) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(@Param("eventKey") String eventKey, @Param("processedAt") LocalDateTime processedAt);

    @Modifying
    @Query("DELETE FROM ProcessedEventEntity e WHERE e.processedAt < :threshold")
    int deleteOlderThan(@Param("threshold") LocalDateTime threshold);
}
