package org.mephi.service;

import org.junit.jupiter.api.Test;
import org.mephi.ApplicationTest;
import org.mephi.TestFactory;
import org.mephi.domain.entity.ShortLink;
import org.mephi.domain.repository.ShortLinkRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidLinkProcessorTest extends ApplicationTest {

    @Autowired
    private InvalidLinkProcessor processor;

    @MockitoSpyBean
    private NotificationService notificationService;

    @Autowired
    private ShortLinkRepository linkRepository;

    // всего сохранено 3 ссылки, одна из них валидная - её не должны трогать
    @Test
    void shouldProcessInvalidLinks() {
        // given
        ShortLink expiredLink = TestFactory.randomShortLinkBuilder()
                .expiresAt(Instant.now().minusSeconds(10L))
                .build();
        ShortLink reachedClickLimitLink = TestFactory.randomShortLinkBuilder()
                .clickLimit(50)
                .clickCount(50)
                .build();
        ShortLink validLink = TestFactory.randomShortLinkBuilder().build();

        ArgumentCaptor<List<ShortLink>> listCaptor = ArgumentCaptor.forClass(List.class);

        linkRepository.saveAll(Arrays.asList(expiredLink, reachedClickLimitLink, validLink));

        // when
        processor.process();

        // then
        Mockito.verify(notificationService, Mockito.times(1))
                .notifyAboutInvalidLinks(listCaptor.capture());

        List<ShortLink> actualInvalidLinks = listCaptor.getValue();
        assertThat(actualInvalidLinks)
                .hasSize(2)
                .anySatisfy(link -> assertThat(link)
                        .usingRecursiveComparison()
                        .withEqualsForType(INSTANT_NEAR_EQUALITY, Instant.class)
                        .isEqualTo(expiredLink))
                .anySatisfy(link -> assertThat(link)
                        .usingRecursiveComparison()
                        .withEqualsForType(INSTANT_NEAR_EQUALITY, Instant.class)
                        .isEqualTo(reachedClickLimitLink));

        List<ShortLink> linksAfterProcessing = linkRepository.findAll();
        assertThat(linksAfterProcessing).hasSize(1);
        assertThat(linksAfterProcessing.get(0).getId()).isEqualTo(validLink.getId());
    }
}
