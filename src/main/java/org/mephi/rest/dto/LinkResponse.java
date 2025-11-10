package org.mephi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mephi.domain.entity.ShortLink;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkResponse {

    @Schema(description = "Идентификатор ссылки", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @Schema(description = "Краткий адрес ссылки", requiredMode = Schema.RequiredMode.REQUIRED, example = "/abcde")
    private String shortUrl;

    @Schema(description = "Полный исходный URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com")
    private String originalUrl;

    @Schema(description = "ID владельца ссылки", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID ownerId;

    @Schema(description = "Время создания ссылки", format = "date-time", example = "2023-10-01T12:00:00Z")
    private Instant createdAt;

    @Schema(description = "Срок окончания действия ссылки", format = "date-time", example = "2023-12-31T23:59:59Z")
    private Instant expiresAt;

    @Schema(description = "Предельное количество кликов", example = "100")
    private Integer clickLimit;

    @Schema(description = "Количество выполненных кликов", example = "80")
    private Integer clickCount;

    @Schema(description = "Активна ли ссылка", example = "true")
    private Boolean active;

    @Schema(description = "Статус ссылки (ACTIVE, EXPIRED, LIMIT_EXCEEDED, DEACTIVATED)")
    private String status;

    public LinkResponse(ShortLink link, String baseUrl) {
        this.id = link.getId();
        this.shortUrl = baseUrl + link.getShortUrl();
        this.originalUrl = link.getOriginalUrl();
        this.ownerId = link.getOwnerId();
        this.createdAt = link.getCreatedAt();
        this.expiresAt = link.getExpiresAt();
        this.clickLimit = link.getClickLimit();
        this.clickCount = link.getClickCount();
        this.active = link.getActive();
        this.status = calculateStatus(link);
    }

    private String calculateStatus(ShortLink link) {
        if (!link.getActive()) {
            return "DEACTIVATED";
        } else if (link.isExpired()) {
            return "EXPIRED";
        } else if (link.isClickLimitExceeded()) {
            return "LIMIT_EXCEEDED";
        } else {
            return "ACTIVE";
        }
    }
}
