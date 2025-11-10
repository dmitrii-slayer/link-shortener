package org.mephi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLimitRequest {

    @Schema(description = "Новый лимит кликов", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", example = "100")
    @NotNull(message = "Лимит не может быть null")
    @Min(value = 1, message = "Лимит должен быть не менее 1")
    private Integer clickLimit;
}
