package org.mephi.rest.controller;

import lombok.RequiredArgsConstructor;
import org.mephi.rest.api.RedirectApi;
import org.mephi.rest.dto.CommonApiResponse;
import org.mephi.service.LinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RedirectController implements RedirectApi {

    private final LinkService linkService;

    @Override
    public ResponseEntity<CommonApiResponse<String>> redirect(String shortUrl) {
        Optional<String> originalUrlOpt = linkService.redirect(shortUrl);

        if (originalUrlOpt.isPresent()) {
            String originalUrl = originalUrlOpt.get();
            CommonApiResponse<String> response = CommonApiResponse.success(originalUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(originalUrl));
            return new ResponseEntity<>(response, headers, HttpStatus.FOUND);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonApiResponse.error("Ссылка не найдена, просрочена или превышен лимит переходов"));
        }
    }
}
