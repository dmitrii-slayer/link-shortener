package org.mephi.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mephi.ApplicationTest;
import org.mephi.TestFactory;
import org.mephi.domain.entity.ShortLink;
import org.mephi.domain.repository.ShortLinkRepository;
import org.mephi.rest.dto.CommonApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RedirectControllerTest extends ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShortLinkRepository linkRepository;

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        // given
        ShortLink savedLink = linkRepository.save(TestFactory.randomShortLinkBuilder().build());

        // when
        var response = mockMvc.perform(get("/{shortUrl}", savedLink.getShortUrl()))
                .andDo(print())
                .andExpect(status().isFound())
                .andReturn().getResponse();

        // then
        assertThat(response.getHeader("location")).isEqualTo(savedLink.getOriginalUrl());
    }

    public static Stream<Arguments> provideInvalidLinks() {
        ShortLink expiredLink = TestFactory.randomShortLinkBuilder()
                .expiresAt(Instant.now().minusSeconds(10L))
                .build();
        ShortLink reachedClickLimitLink = TestFactory.randomShortLinkBuilder()
                .clickCount(20)
                .clickLimit(20)
                .build();

        return Stream.of(expiredLink, reachedClickLimitLink)
                .map(Arguments::of);
    }

    @MethodSource("provideInvalidLinks")
    @ParameterizedTest
    void shouldReturnNotFoundIfLinkIsNotValid(ShortLink invalidLink) throws Exception {
        // given
        linkRepository.save(invalidLink);

        var expectedResponse = new CommonApiResponse<>(
                CommonApiResponse.ResponseStatus.ERROR,
                "Ссылка не найдена, просрочена или превышен лимит переходов",
                null);

        // when
        String responseBody = mockMvc.perform(get("/{shortUrl}", invalidLink.getShortUrl()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        CommonApiResponse<String> actualResponse = objectMapper.readValue(responseBody, new TypeReference<CommonApiResponse<String>>() {
        });

        // then
        assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }
}
