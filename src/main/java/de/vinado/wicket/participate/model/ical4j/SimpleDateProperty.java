package de.vinado.wicket.participate.model.ical4j;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SimpleDateProperty extends Property {

    private Date date;

    public SimpleDateProperty(final Type type, final Date date) {
        super(type.toString(), PropertyFactoryImpl.getInstance());

        if (type.equals(Type.DTEND)) {
            this.date = DateUtils.addDays(date, 1);
        } else {
            this.date = date;
        }
    }

    @Override
    public void setValue(final String aValue) throws ParseException {

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        this.date = simpleDateFormat.parse(aValue);
    }

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    public String getValue() {
        return new SimpleDateFormat("yyyMMdd").format(date);
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
