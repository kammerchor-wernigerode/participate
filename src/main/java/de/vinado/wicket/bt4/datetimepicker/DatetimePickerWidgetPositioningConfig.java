package de.vinado.wicket.bt4.datetimepicker;

import de.agilecoders.wicket.jquery.AbstractConfig;
import de.agilecoders.wicket.jquery.IKey;

/**
 * @author Vincent Nadoll
 */
public class DatetimePickerWidgetPositioningConfig extends AbstractConfig {

    private static final long serialVersionUID = -2867685352511530316L;

    private static final IKey<String> Horizontal = newKey("horizontal", "auto");
    private static final IKey<String> Vertical = newKey("vertical", "auto");

    public DatetimePickerWidgetPositioningConfig withHorizontalPositioning(String positioning) {
        put(Horizontal, positioning);
        return this;
    }

    public DatetimePickerWidgetPositioningConfig withVerticalPositioning(String positioning) {
        put(Vertical, positioning);
        return this;
    }
}
