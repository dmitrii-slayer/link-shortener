package org.mephi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi.domain.entity.ShortLink;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String LINK_NO_LONGER_VALID_NOTIFICATION_TEMPLATE = """
            Здравствуйсте!
            У вашей ссылки закончилось время действия либо исчерпан лимит переходов.
            Короткая ссылка: {}
            Оригинальная ссылка: {}
            """;

    public void notifyAboutInvalidLinks(List<ShortLink> invalidLinks) {
        // имитация отправки уведомлений пользователям
        invalidLinks.forEach(link -> log.info(LINK_NO_LONGER_VALID_NOTIFICATION_TEMPLATE,
                link.getShortUrl(),
                link.getOriginalUrl()));
    }
}
