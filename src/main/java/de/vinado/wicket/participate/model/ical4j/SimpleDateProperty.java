package de.vinado.wicket.participate.model.ical4j;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
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

    public SimpleDateProperty(Type type) {
        super(type.toString(), new Factory(type));
    }

    public SimpleDateProperty(Type type, Date date) {
        super(type.toString(), new Factory(type));

        if (type.equals(Type.DTEND)) {
            this.date = DateUtils.addDays(date, 1);
        } else {
            this.date = date;
        }
    }

    public SimpleDateProperty(Type type, ParameterList parameters, String value) throws ParseException {
        super(type.toString(), parameters, new Factory(type));
        setValue(value);
    }

    @Override
    public void setValue(String aValue) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
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

        Type(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }


    private static final class Factory extends Content.Factory implements PropertyFactory<SimpleDateProperty> {

        private final Type type;

        private Factory(Type type) {
            super(type.toString());
            this.type = type;
        }

        @Override
        public SimpleDateProperty createProperty() {
            return new SimpleDateProperty(type);
        }

        @Override
        public SimpleDateProperty createProperty(ParameterList parameters, String value) throws ParseException {
            return new SimpleDateProperty(type, parameters, value);
        }
    }
}
