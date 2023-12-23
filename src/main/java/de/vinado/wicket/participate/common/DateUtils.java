package de.vinado.wicket.participate.common;

import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

public final class DateUtils {

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

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(new Date(date.getTime()));
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(new Date(date.getTime()));
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
