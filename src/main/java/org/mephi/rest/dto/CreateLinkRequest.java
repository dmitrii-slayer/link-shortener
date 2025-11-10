package org.mephi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLinkRequest {

    @Schema(description = "Исходный URL", example = "https://example.com")
    @NotBlank(message = "URL не может быть пустым")
    @Pattern(regexp = "^(https?)://.*$", message = "URL должен начинаться с http:// или https://")
    private String originalUrl;

    @Schema(description = "Имя пользователя", example = "my_user_1")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Schema(description = "Максимальное число кликов по ссылке (необязательно)", example = "100")
    @Nullable
    private Integer clickLimit;
}
