package org.mephi.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.mephi.domain.entity.ShortLink;
import org.mephi.domain.repository.ShortLinkRepository;
import org.mephi.util.TimeUnitConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {

    @Value("${link.ttl.unit}")
    private String ttlUnit;

    private ChronoUnit ttlChronoUnit;

    @Value("${link.ttl.value}")
    private int ttlValue;

    @Value("${link.default-click-limit:100}")
    private int defaultClickLimit;

    private final ShortLinkRepository shortLinkRepository;
    private final UrlShortenerService urlShortenerService;

    @PostConstruct
    public void init() {
        ttlChronoUnit = TimeUnitConverter.toChronoUnit(ttlUnit);
    }

    public ShortLink createShortLink(String originalUrl, UUID ownerId, Integer customClickLimit) {
        String shortUrl;
        do {
            shortUrl = urlShortenerService.generateShortUrl();
        } while (shortLinkRepository.existsByShortUrl(shortUrl));

        Instant expiresAt = Instant.now().plus(ttlValue, ttlChronoUnit);

        int clickLimit = customClickLimit != null ? customClickLimit: defaultClickLimit;

        ShortLink newLink = new ShortLink(
                shortUrl,
                originalUrl,
                ownerId,
                expiresAt,
                clickLimit
        );

        return shortLinkRepository.save(newLink);
    }

    public Optional<String> redirect(String shortUrl) {
        Optional<ShortLink> linkOpt = shortLinkRepository.findByShortUrl(shortUrl);

        if (linkOpt.isEmpty()) {
            return Optional.empty();
        }

        ShortLink link = linkOpt.get();

        if (!link.isValid()) {
            return Optional.empty();
        }

        link.incrementClickCount();
        shortLinkRepository.save(link);

        return Optional.of(link.getOriginalUrl());
    }

    public List<ShortLink> getUserLinks(UUID userId) {
        return shortLinkRepository.findByOwnerId(userId);
    }

    public Optional<ShortLink> getUserLink(UUID userId, String shortUrl) {
        return shortLinkRepository.findByOwnerIdAndShortUrl(userId, shortUrl);
    }

    public Optional<ShortLink> updateClickLimit(UUID userId, String shortUrl, int newLimit) {
        Optional<ShortLink> linkOpt = shortLinkRepository
                .findByOwnerIdAndShortUrl(userId, shortUrl);

        if (linkOpt.isPresent()) {
            ShortLink link = linkOpt.get();
            link.setClickLimit(newLimit);
            return Optional.of(shortLinkRepository.save(link));
        }

        return Optional.empty();
    }

    public List<ShortLink> findInvalidLinks() {
        return shortLinkRepository.findAllExpiredOrReachedLimit(Instant.now());
    }

    @Transactional
    public boolean deleteLink(UUID userId, String shortUrl) {
        Optional<ShortLink> linkOpt = shortLinkRepository
                .findByOwnerIdAndShortUrl(userId, shortUrl);

        if (linkOpt.isPresent()) {
            shortLinkRepository.delete(linkOpt.get());
            return true;
        }

        return false;
    }

    @Transactional
    public void deleteLinks(List<ShortLink> links) {
        shortLinkRepository.deleteAll(links);
    }
}
