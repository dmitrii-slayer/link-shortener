package org.mephi.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mephi.rest.dto.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Переадресация", description = "Переход по коротким ссылкам")
public interface RedirectApi {

    @Operation(
            summary = "Переход по короткой ссылке",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            content = @Content(mediaType = APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ссылка не найдена, просрочена или превышен лимит переходов"
                    )
            })
    @GetMapping("/{shortUrl}")
    ResponseEntity<CommonApiResponse<String>> redirect(@PathVariable("shortUrl") String shortUrl);
}
