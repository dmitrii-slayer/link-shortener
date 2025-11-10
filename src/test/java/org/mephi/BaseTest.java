package org.mephi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.BiPredicate;

public abstract class BaseTest {

    protected static ObjectMapper objectMapper = setUpObjectMapper();

    private static ObjectMapper setUpObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return om;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T copyObject(T object) {
        return (T) objectMapper.readValue(objectMapper.writeValueAsString(object), object.getClass());
    }

    /**
     * для корректного сравнения при разной точности либо при каких-то округлениях <br>
     * пример ошибки которой позволяет избежать: <br>
     * field/property 'data[0].createdAt' differ: <br>
     * - actual value  : 2025-11-10T16:30:56.455402Z <br>
     * - expected value: 2025-11-10T16:30:56.455401600Z <br>
     * Compared objects have java types and were thus compared with equals method
     */
    public static final BiPredicate<Instant, Instant> INSTANT_NEAR_EQUALITY =
            (instant1, instant2) -> {
                if (instant1 == null && instant2 == null) return true;
                if (instant1 == null || instant2 == null) return false;

                long diffInMillis = Math.abs(instant1.until(instant2, ChronoUnit.MILLIS));
                return diffInMillis <= 1L;
            };
}
