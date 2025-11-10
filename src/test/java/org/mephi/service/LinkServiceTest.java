package org.mephi.service;

import org.junit.jupiter.api.Test;
import org.mephi.ApplicationTest;
import org.mephi.domain.entity.ShortLink;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mephi.TestFactory.randomUrl;

class LinkServiceTest extends ApplicationTest {

    @Autowired
    private LinkService linkService;

    @Test
    void shouldCreateWithDefaultClickLimitIfNotSet() {
        ShortLink createdLink = linkService.createShortLink(randomUrl(), UUID.randomUUID(), null);
        assertThat(createdLink.getClickLimit()).isEqualTo(300);
    }
}
