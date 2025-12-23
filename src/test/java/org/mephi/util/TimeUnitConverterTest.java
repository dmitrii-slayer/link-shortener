package org.mephi.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeUnitConverterTest {

    @CsvSource({
            "hours, HOURS",
            "minutes, MINUTES",
            "seconds, SECONDS"
    })
    @ParameterizedTest
    void shouldReturnChronoUnit(String input, ChronoUnit expected) {
        ChronoUnit result = TimeUnitConverter.toChronoUnit(input);
        assertThat(result).isEqualTo(expected);
    }

    @NullAndEmptySource
    @ValueSource(strings = {"invalid", "days", "weeks"})
    @ParameterizedTest
    void shouldThrowExceptionOnInvalidInput(String input) {
        assertThatThrownBy(() -> TimeUnitConverter.toChronoUnit(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Недопустимая единица времени: " + input);
    }
}
