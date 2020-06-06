package de.vinado.wicket.participate.common;

import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * @author Vincent Nadoll
 */
public final class DateUtils {

    /**
     * Converts a {@link Date} into a {@link LocalDate}.
     */
    public static LocalDate toLocalDate(@Nullable Date date) {
        return Optional.ofNullable(date)
            .map(Date::getTime)
            .map(Instant::ofEpochMilli)
            .map(DateUtils::atDefaultZone)
            .map(ZonedDateTime::toLocalDate)
            .orElse(null);
    }

    private static ZonedDateTime atDefaultZone(Instant instant) {
        return instant.atZone(ZoneId.systemDefault());
    }
}
