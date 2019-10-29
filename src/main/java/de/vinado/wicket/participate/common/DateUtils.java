package de.vinado.wicket.participate.common;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Vincent Nadoll
 */
@UtilityClass
public class DateUtils {


    /**
     * Converts a {@link Date} into a {@link LocalDate}.
     */
    public static LocalDate convert(Date date) {
        return Instant.ofEpochMilli(date.getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }
}
