package org.mephi;

import org.junit.jupiter.api.BeforeEach;
import org.mephi.domain.repository.ShortLinkRepository;
import org.mephi.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApplicationTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShortLinkRepository linkRepository;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
        linkRepository.deleteAll();
    }
}
