package de.kammerchorwernigerode.app.participate.wicket.bootstrap.icon;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.image.IconType;

public class Bi extends IconType {

    public static final Bi calendar_fill = new Bi("calendar-fill");

    public Bi(String cssClassName) {
        super(cssClassName);
    }

    @Override
    public String cssClassName() {
        return "bi bi-" + getCssClassName();
    }
}
