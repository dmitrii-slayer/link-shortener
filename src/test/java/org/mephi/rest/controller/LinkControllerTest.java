package org.mephi.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.mephi.ApplicationTest;
import org.mephi.TestFactory;
import org.mephi.domain.entity.ShortLink;
import org.mephi.domain.entity.User;
import org.mephi.domain.repository.ShortLinkRepository;
import org.mephi.domain.repository.UserRepository;
import org.mephi.rest.dto.CommonApiResponse;
import org.mephi.rest.dto.CreateLinkRequest;
import org.mephi.rest.dto.LinkResponse;
import org.mephi.rest.dto.UpdateLimitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LinkControllerTest extends ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShortLinkRepository linkRepository;

    @Test
    void shouldCreateShortLinkSuccessfully() throws Exception {
        // given
        CreateLinkRequest request = CreateLinkRequest.builder()
                .originalUrl("https://my-link.com")
                .username("create-link-user")
                .clickLimit(10)
                .build();

        LinkResponse expectedLink = LinkResponse.builder()
                .originalUrl(request.getOriginalUrl())
                .clickLimit(request.getClickLimit())
                .clickCount(0)
                .active(true)
                .status("ACTIVE")
                .build();

        var expectedResponse = new CommonApiResponse<>(
                CommonApiResponse.ResponseStatus.SUCCESS, "Ссылка успешно создана", expectedLink);

        // when
        String responseBody = mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommonApiResponse<LinkResponse> actualResponse = objectMapper.readValue(responseBody,
                new TypeReference<>() {
                });
        LinkResponse actualLink = actualResponse.getData();

        // then
        assertThat(actualResponse.getStatus()).isEqualTo(CommonApiResponse.ResponseStatus.SUCCESS);
        assertThat(actualResponse.getMessage()).isEqualTo("Ссылка успешно создана");

        assertThat(actualLink)
                .usingRecursiveComparison()
                .ignoringFields("id", "shortUrl", "ownerId", "createdAt", "expiresAt")
                .isEqualTo(expectedResponse.getData());

        assertThat(actualLink.getId()).isNotNull();
        assertThat(actualLink.getCreatedAt())
                .isCloseTo(Instant.now(), within(10L, ChronoUnit.SECONDS));
        assertThat(actualLink.getExpiresAt())
                .isCloseTo(Instant.now().plus(2L, ChronoUnit.HOURS),
                        within(10L, ChronoUnit.SECONDS));
        assertThat(actualLink.getShortUrl()).hasSize(30); // http://localhost:8080/ + 8 символов
        assertThat(actualLink.getOwnerId()).isNotNull();

        User createdUser = userRepository.findById(actualLink.getOwnerId()).orElseThrow();
        assertThat(createdUser.getUsername()).isEqualTo(request.getUsername());
        assertThat(createdUser.getCreatedAt()).isCloseTo(Instant.now(), within(10L, ChronoUnit.SECONDS));
    }

    @Test
    void shouldGetUserLinksSuccessfully() throws Exception {
        // given
        User savedUser = userRepository.save(new User("get-user-links-user"));
        ShortLink savedLink = linkRepository.save(TestFactory.randomShortLinkBuilder()
                .ownerId(savedUser.getId())
                .build());

        var expectedLinks = Collections.singletonList(
                new LinkResponse(savedLink, "http://localhost:8080/"));

        var expectedResponse = new CommonApiResponse<>(
                CommonApiResponse.ResponseStatus.SUCCESS, "Success", expectedLinks);

        // when
        String responseBody = mockMvc.perform(get("/api/links/user/{username}",
                        savedUser.getUsername()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommonApiResponse<List<LinkResponse>> actualResponse = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        // then
        assertThat(actualResponse)
                .usingRecursiveComparison()
                .withEqualsForType(INSTANT_NEAR_EQUALITY, Instant.class)
                .isEqualTo(expectedResponse);
    }

    @Test
    void shouldUpdateClickLimitSuccessfully() throws Exception {
        // given
        int newClickLimit = 555;
        User savedUser = userRepository.save(new User("update-click-limit-user"));
        ShortLink savedLink = linkRepository.save(TestFactory.randomShortLinkBuilder()
                .ownerId(savedUser.getId())
                .clickLimit(20)
                .build());
        UpdateLimitRequest request = new UpdateLimitRequest(newClickLimit);

        ShortLink expectedLink = copyObject(savedLink).toBuilder().clickLimit(newClickLimit).build();

        var expectedResponse = new CommonApiResponse<>(
                CommonApiResponse.ResponseStatus.SUCCESS,
                "Лимит обновлен",
                new LinkResponse(expectedLink, "http://localhost:8080/")
        );

        // when
        String responseBody = mockMvc.perform(put("/api/links/{short-url}/user/{username}/limit",
                        savedLink.getShortUrl(), savedUser.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommonApiResponse<LinkResponse> actualResponse = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        // then
        assertThat(actualResponse)
                .usingRecursiveComparison()
                .withEqualsForType(INSTANT_NEAR_EQUALITY, Instant.class)
                .isEqualTo(expectedResponse);
    }

    @Test
    void shouldDeleteLinkSuccessfully() throws Exception {
        // given
        User savedUser = userRepository.save(new User("delete-user-links-user"));
        ShortLink savedLink = linkRepository.save(TestFactory.randomShortLinkBuilder()
                .ownerId(savedUser.getId())
                .build());

        // when
        mockMvc.perform(delete("/api/links/{short-url}/user/{username}",
                        savedLink.getShortUrl(), savedUser.getUsername()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ссылка удалена"));

        // then
        assertThat(linkRepository.findById(savedLink.getId())).isEmpty();
    }

    @Test
    void shouldGetLinkInfoSuccessfully() throws Exception {
        // given
        User savedUser = userRepository.save(new User("get-link-info-user"));
        ShortLink savedLink = linkRepository.save(TestFactory.randomShortLinkBuilder()
                .ownerId(savedUser.getId())
                .build());

        var expectedResponse = new CommonApiResponse<>(
                CommonApiResponse.ResponseStatus.SUCCESS,
                "Success",
                new LinkResponse(savedLink, "http://localhost:8080/")
        );

        // when
        String responseBody = mockMvc.perform(get("/api/links/{short-url}/user/{username}/info",
                        savedLink.getShortUrl(), savedUser.getUsername()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        CommonApiResponse<LinkResponse> actualResponse = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        // then
        assertThat(actualResponse)
                .usingRecursiveComparison()
                .withEqualsForType(INSTANT_NEAR_EQUALITY, Instant.class)
                .isEqualTo(expectedResponse);
    }
}
