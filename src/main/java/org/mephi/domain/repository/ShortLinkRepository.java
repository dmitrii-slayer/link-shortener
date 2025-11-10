package org.mephi.domain.repository;

import org.mephi.domain.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShortLinkRepository extends JpaRepository<ShortLink, UUID> {

    Optional<ShortLink> findByShortUrl(String shortUrl);

    List<ShortLink> findByOwnerId(UUID ownerId);

    Optional<ShortLink> findByOwnerIdAndShortUrl(UUID ownerId, String shortUrl);

    @Query("SELECT l FROM ShortLink l WHERE l.expiresAt < :datetime OR l.clickCount >= l.clickLimit")
    List<ShortLink> findAllExpiredOrReachedLimit(Instant datetime);

    boolean existsByShortUrl(String shortUrl);
}
