package de.vinado.wicket.bt4.datetimepicker;

import de.agilecoders.wicket.jquery.AbstractConfig;
import de.agilecoders.wicket.jquery.IKey;

public class DatetimePickerWidgetPositioningConfig extends AbstractConfig {

    private static final IKey<String> Vertical = newKey("vertical", "auto");

    public DatetimePickerWidgetPositioningConfig withVerticalPositioning(String positioning) {
        put(Vertical, positioning);
        return this;
    }
}
