package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconType;

public class Bi extends IconType {

    public static final Bi calendar_fill = new Bi("calendar-fill");
    public static final Bi check = new Bi("check");
    public static final Bi chevron_double_down = new Bi("chevron-double-down");
    public static final Bi chevron_expand = new Bi("chevron-expand");
    public static final Bi chevron_double_up = new Bi("chevron-double-up");
    public static final Bi circle = new Bi("circle");
    public static final Bi circle_fill = new Bi("circle-fill");
    public static final Bi people_fill = new Bi("people-fill");
    public static final Bi plus_lg = new Bi("plus-lg");
    public static final Bi question = new Bi("question");
    public static final Bi x = new Bi("x");

    public Bi(String cssClassName) {
        super(cssClassName);
    }

    @Override
    public String cssClassName() {
        return "bi bi-" + getCssClassName();
    }
}
