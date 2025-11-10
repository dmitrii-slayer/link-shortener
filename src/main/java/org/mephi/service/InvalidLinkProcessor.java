package org.mephi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi.domain.entity.ShortLink;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvalidLinkProcessor {

    private final LinkService linkService;
    private final NotificationService notificationService;

    public void process() {
        List<ShortLink> invalidLinks = linkService.findInvalidLinks();
        log.info("Найдено невалидных ссылок: {}", invalidLinks.size());

        notificationService.notifyAboutInvalidLinks(invalidLinks);
        linkService.deleteLinks(invalidLinks);
    }
}
