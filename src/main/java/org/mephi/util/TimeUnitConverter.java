package org.mephi.util;

import java.time.temporal.ChronoUnit;

public class TimeUnitConverter {

    public static ChronoUnit toChronoUnit(String timeUnit) {
        if ("hours".equalsIgnoreCase(timeUnit)) {
            return ChronoUnit.HOURS;
        } else if ("minutes".equalsIgnoreCase(timeUnit)) {
            return ChronoUnit.MINUTES;
        } else if ("seconds".equalsIgnoreCase(timeUnit)) {
            return ChronoUnit.SECONDS;
        }
        throw new IllegalArgumentException("Недопустимая единица времени: " + timeUnit);
    }
}
