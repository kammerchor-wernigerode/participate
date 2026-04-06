package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.image.IconType;

public class Bi extends IconType {

    public static final Bi calendar_fill = new Bi("calendar-fill");
    public static final Bi chevron_double_down = new Bi("chevron-double-down");
    public static final Bi chevron_expand = new Bi("chevron-expand");
    public static final Bi chevron_double_up = new Bi("chevron-double-up");

    public Bi(String cssClassName) {
        super(cssClassName);
    }

    @Override
    public String cssClassName() {
        return "bi bi-" + getCssClassName();
    }
}
