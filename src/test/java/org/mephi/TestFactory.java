package org.mephi;

import org.apache.commons.lang3.RandomStringUtils;
import org.mephi.domain.entity.ShortLink;

import java.time.Instant;
import java.util.UUID;

public class TestFactory {

    private TestFactory() {
    }

    public static ShortLink.ShortLinkBuilder randomShortLinkBuilder() {
        return ShortLink.builder()
                .ownerId(UUID.randomUUID())
                .active(true)
                .clickCount(3)
                .clickLimit(50)
                .originalUrl(randomUrl())
                .shortUrl(randomShortUrl())
                .createdAt(Instant.now().minusSeconds(30L))
                .expiresAt(Instant.now().plusSeconds(120L));
    }

    public static String randomUrl() {
        return String.format("https://www.%s.ru", RandomStringUtils.insecure().nextAlphabetic(6).toLowerCase());
    }

    private static String randomShortUrl() {
        return RandomStringUtils.insecure().nextAlphanumeric(8);
    }
}
