package com.hexocnc.lead.listener;

import com.hexocnc.event.LeadCreatedEvent;
import com.hexocnc.lead.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadCreatedEventListener {

    private final LeadService leadService;

    @RabbitListener(queues = "${app.rabbit.lead-created.queue}")
    public void onLeadCreated(final LeadCreatedEvent event) {
        log.info("Received LeadCreatedEvent for {} lead '{}'", event.domain(), event.name());
        leadService.createLeadFromEvent(event);
    }
}
