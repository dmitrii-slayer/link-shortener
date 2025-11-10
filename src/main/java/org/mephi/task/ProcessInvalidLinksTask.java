package org.mephi.task;

import lombok.RequiredArgsConstructor;
import org.mephi.service.InvalidLinkProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        value = "invalid-link-processing.schedule.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class ProcessInvalidLinksTask {

    private final InvalidLinkProcessor processor;

    @Scheduled(cron = "${link.process-invalid.cron}")
    public void processInvalidLinks() {
        processor.process();
    }
}
