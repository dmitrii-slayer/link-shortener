package org.mephi.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "short_links")
public class ShortLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "short_url", nullable = false, unique = true, length = 50)
    private String shortUrl;

    @Column(name = "original_url", nullable = false, length = 2000)
    private String originalUrl;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "click_limit", nullable = false)
    private Integer clickLimit;

    @Builder.Default
    @Column(name = "click_count", nullable = false)
    private Integer clickCount = 0;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    public ShortLink(String shortUrl, String originalUrl, UUID ownerId,
                     Instant expiresAt, Integer clickLimit) {
        this();
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.ownerId = ownerId;
        this.expiresAt = expiresAt;
        this.clickLimit = clickLimit;
    }

    @JsonIgnore
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    @JsonIgnore
    public boolean isClickLimitExceeded() {
        return clickCount >= clickLimit;
    }

    @JsonIgnore
    public boolean isValid() {
        return active && !isExpired() && !isClickLimitExceeded();
    }

    @JsonIgnore
    public void incrementClickCount() {
        this.clickCount++;
    }
}
