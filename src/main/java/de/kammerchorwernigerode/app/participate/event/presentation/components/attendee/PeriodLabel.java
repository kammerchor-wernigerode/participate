package de.kammerchorwernigerode.app.participate.event.presentation.components.attendee;

import de.kammerchorwernigerode.app.participate.event.presentation.AttendeePeriodPrinter;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeProjection;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventDates;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PeriodLabel extends Label {

    private final IModel<? extends AttendeeProjection> attendee;

    @SpringBean
    private AttendeePeriodPrinter attendeePeriodPrinter;

    public PeriodLabel(String id, IModel<? extends AttendeeProjection> attendee, IModel<? extends EventDates> event) {
        super(id);
        this.attendee = attendee;

        setDefaultModel(attendee.combineWith(event, this::print));
    }

    public IModel<String> printDates() {
        return attendee.map(this::printRange);
    }

    private String printRange(AttendeeProjection attendee) {
        Locale locale = getLocale();
        return attendeePeriodPrinter.print(attendee, locale);
    }

    private String print(AttendeeProjection attendee, EventDates event) {
        AttendanceLabel label = create(attendee, event);

        if (!label.hasCustomFrom() && !label.hasCustomTo()) {
            return getString("attendance.full");
        }

        Map<String, Object> vars = new HashMap<>();
        vars.put("from", getString(label.from().resourceKey()));
        vars.put("to", getString(label.to().resourceKey()));

        String key = switch ((label.hasCustomFrom() ? 1 : 0) + (label.hasCustomTo() ? 2 : 0)) {
            case 1 -> "attendance.from";
            case 2 -> "attendance.to";
            case 3 -> "attendance.fromTo";
            default -> "attendance.full";
        };

        return getString(key, () -> vars);
    }

    private AttendanceLabel create(AttendeeProjection attendee, EventDates event) {
        ZonedDateTime eventStart = event.getStartInstant().atZone(event.getStartZoneId());
        ZonedDateTime eventEnd = event.getEndInstant().atZone(event.getEndZoneId());
        LocalDateTime from = attendee.getFromDateTime();
        LocalDateTime to = attendee.getToDateTime();

        boolean hasCustomFrom = !from.equals(eventStart.toLocalDateTime());
        boolean hasCustomTo = !to.equals(eventEnd.toLocalDateTime());

        DayPeriodLabel fromLabel = new DayPeriodLabel(from.getDayOfWeek(), toDayPeriod(from));
        DayPeriodLabel toLabel = new DayPeriodLabel(to.getDayOfWeek(), toDayPeriod(to));
        return new AttendanceLabel(hasCustomFrom, hasCustomTo, fromLabel, toLabel);
    }

    private DayPeriod toDayPeriod(LocalDateTime dateTime) {
        return switch (dateTime.getHour()) {
            case 0, 1, 2, 3, 4, 5 -> DayPeriod.NIGHT;
            case 6, 7, 8, 9, 10, 11 -> DayPeriod.MORNING;
            case 12, 13, 14, 15, 16 -> DayPeriod.AFTERNOON;
            case 17, 18, 19, 20, 21, 22, 23 -> DayPeriod.EVENING;
            default -> throw new IllegalStateException();
        };
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        attendee.detach();
    }


    private record AttendanceLabel(boolean hasCustomFrom, boolean hasCustomTo, DayPeriodLabel from, DayPeriodLabel to) {
    }

    private record DayPeriodLabel(DayOfWeek dayOfWeek, DayPeriod period) {

        public String resourceKey() {
            return String.format("dayPeriod.%s.%s", dayOfWeek, period);
        }
    }

    private enum DayPeriod {

        NIGHT,
        MORNING,
        AFTERNOON,
        EVENING,
        ;
    }
}
