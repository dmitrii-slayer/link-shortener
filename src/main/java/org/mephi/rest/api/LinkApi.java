package org.mephi.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.mephi.rest.dto.CommonApiResponse;
import org.mephi.rest.dto.CreateLinkRequest;
import org.mephi.rest.dto.LinkResponse;
import org.mephi.rest.dto.UpdateLimitRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Tag(name = "Ссылки", description = "Управление короткими ссылками пользователей")
public interface LinkApi {

    @Operation(summary = "Создание короткой ссылки",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE))
            })
    @PostMapping
    ResponseEntity<CommonApiResponse<LinkResponse>> createShortLink(@Valid @RequestBody CreateLinkRequest request);

    @Operation(summary = "Получение всех ссылок пользователя",
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE))})
    @GetMapping("/user/{username}")
    ResponseEntity<CommonApiResponse<List<LinkResponse>>> getUserLinks(@PathVariable("username") String username);

    @Operation(summary = "Обновление лимита переходов по ссылке",
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE))})
    @PutMapping("/{shortUrl}/user/{username}/limit")
    ResponseEntity<CommonApiResponse<LinkResponse>> updateClickLimit(
            @PathVariable("shortUrl") String shortUrl,
            @PathVariable("username") String username,
            @Valid @RequestBody UpdateLimitRequest request);

    @Operation(summary = "Удаление короткой ссылки",
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE))})
    @DeleteMapping("/{shortUrl}/user/{username}")
    ResponseEntity<CommonApiResponse<Void>> deleteLink(
            @PathVariable("shortUrl") String shortUrl,
            @PathVariable("username") String username);

    @Operation(summary = "Получение информации о ссылке",
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE))})
    @GetMapping("/{shortUrl}/user/{username}/info")
    ResponseEntity<CommonApiResponse<LinkResponse>> getLinkInfo(
            @PathVariable("shortUrl") String shortUrl,
            @PathVariable("username") String username);
}
