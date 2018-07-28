package de.vinado.wicket.participate.model.ical4j;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.validate.ValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SimpleDateProperty extends Property {

    private Date date;

    public SimpleDateProperty(final Type type, final Date date) {
        super(type.toString(), PropertyFactoryImpl.getInstance());

        if (type.equals(Type.DTEND)) {
            this.date = addDays(date, 1);
        } else {
            this.date = date;
        }
    }

    private static Date addDays(final Date date, final int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    @Override
    public void setValue(final String aValue) throws ParseException {

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMdd");
        this.date = simpleDateFormat.parse(aValue);
    }

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    public String getValue() {
        return new SimpleDateFormat("YYYYMMdd").format(date);
    }

    public enum Type {
        DTSTART("DTSTART"),
        DTEND("DTEND");

        private String type;

        Type(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
