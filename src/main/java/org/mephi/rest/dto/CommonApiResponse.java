package org.mephi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.mephi.rest.dto.CommonApiResponse.ResponseStatus.ERROR;
import static org.mephi.rest.dto.CommonApiResponse.ResponseStatus.SUCCESS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonApiResponse<T> {

    @Schema(description = "Статусы ответа (SUCCESS, ERROR)", allowableValues = {"SUCCESS", "ERROR"})
    private ResponseStatus status;

    @Schema(description = "Сообщение ответа", example = "Операция выполнена успешно")
    private String message;

    @Schema(description = "Данные ответа (если имеются)")
    private T data;

    public static <T> CommonApiResponse<T> success(T data) {
        return new CommonApiResponse<>(SUCCESS, "Success", data);
    }

    public static <T> CommonApiResponse<T> success(String message, T data) {
        return new CommonApiResponse<>(SUCCESS, message, data);
    }

    public static <T> CommonApiResponse<T> error(String message) {
        return new CommonApiResponse<>(ERROR, message, null);
    }

    public enum ResponseStatus {
        SUCCESS, ERROR
    }
}
