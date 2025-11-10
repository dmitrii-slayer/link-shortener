package org.mephi.rest.controller;

import lombok.RequiredArgsConstructor;
import org.mephi.domain.entity.ShortLink;
import org.mephi.domain.entity.User;
import org.mephi.rest.api.LinkApi;
import org.mephi.rest.dto.CommonApiResponse;
import org.mephi.rest.dto.CreateLinkRequest;
import org.mephi.rest.dto.LinkResponse;
import org.mephi.rest.dto.UpdateLimitRequest;
import org.mephi.service.LinkService;
import org.mephi.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/links")
public class LinkController implements LinkApi {

    private final UserService userService;
    private final LinkService linkService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public ResponseEntity<CommonApiResponse<LinkResponse>> createShortLink(CreateLinkRequest request) {
        User user = userService.getOrCreateUser(request.getUsername());
        ShortLink link = linkService.createShortLink(
                request.getOriginalUrl(),
                user.getId(),
                request.getClickLimit()
        );

        LinkResponse response = new LinkResponse(link, baseUrl);
        return ResponseEntity.ok(CommonApiResponse.success("Ссылка успешно создана", response));
    }

    @Override
    public ResponseEntity<CommonApiResponse<List<LinkResponse>>> getUserLinks(String username) {
        User user = userService.getOrCreateUser(username);
        List<ShortLink> links = linkService.getUserLinks(user.getId());

        List<LinkResponse> response = links.stream()
                .map(link -> new LinkResponse(link, baseUrl))
                .toList();

        return ResponseEntity.ok(CommonApiResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonApiResponse<LinkResponse>> updateClickLimit(
            String shortUrl, String username, UpdateLimitRequest request) {
        User user = userService.getOrCreateUser(username);
        Optional<ShortLink> updatedLink = linkService.updateClickLimit(
                user.getId(), shortUrl, request.getClickLimit());

        if (updatedLink.isPresent()) {
            LinkResponse response = new LinkResponse(updatedLink.get(), baseUrl);
            return ResponseEntity.ok(CommonApiResponse.success("Лимит обновлен", response));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonApiResponse.error("Ссылка не найдена или у вас нет прав для ее изменения"));
        }
    }

    @Override
    public ResponseEntity<CommonApiResponse<Void>> deleteLink(String shortUrl, String username) {
        User user = userService.getOrCreateUser(username);
        boolean deleted = linkService.deleteLink(user.getId(), shortUrl);

        if (deleted) {
            return ResponseEntity.ok(CommonApiResponse.success("Ссылка удалена", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonApiResponse.error("Ссылка не найдена или у вас нет прав для ее удаления"));
        }
    }

    @Override
    public ResponseEntity<CommonApiResponse<LinkResponse>> getLinkInfo(String shortUrl, String username) {
        User user = userService.getOrCreateUser(username);
        Optional<ShortLink> link = linkService.getUserLink(user.getId(), shortUrl);

        if (link.isPresent()) {
            LinkResponse response = new LinkResponse(link.get(), baseUrl);
            return ResponseEntity.ok(CommonApiResponse.success(response));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonApiResponse.error("Ссылка не найдена"));
        }
    }
}
