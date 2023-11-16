package de.vinado.wicket.bt4.datetimepicker;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;

/**
 * @author Vincent Nadoll
 */
public class DatetimePickerIconConfig extends de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerIconConfig {

    public DatetimePickerIconConfig() {
        useTimeIcon(FontAwesome5IconType.clock_r);
        useDateIcon(FontAwesome5IconType.calendar_r);
        useUpIcon(FontAwesome5IconType.arrow_alt_circle_up_r);
        useDownIcon(FontAwesome5IconType.arrow_alt_circle_down_r);
        usePreviousIcon(FontAwesome5IconType.chevron_left_s);
        useNextIcon(FontAwesome5IconType.chevron_right_s);
        useTodayIcon(FontAwesome5IconType.calendar_check_r);
        useClearIcon(FontAwesome5IconType.trash_alt_r);
        useCloseIcon(FontAwesome5IconType.times_circle_r);
    }
}
