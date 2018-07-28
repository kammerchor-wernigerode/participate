package de.vinado.wicket.participate.common;

import de.vinado.wicket.participate.services.DataService;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AbstractEntityGenerator<T> implements Serializable {

    public abstract T generate(final DataService dataService);

    public void generate(final DataService dataService,
                         final Collection<T> collection, final long count) {
        for (int i = 0; i < count; i++) {
            collection.add(generate(dataService));
        }
    }

    int rint(final int min, final int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    String randomString(final String[] choices) {
        return choices[rint(0, choices.length)];
    }

    Date generateDate() {
        GregorianCalendar gc = new GregorianCalendar();

        int year = rint(2017, 2028);
        gc.set(Calendar.YEAR, year);

        int dayOfYear = rint(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
        gc.set(Calendar.DAY_OF_YEAR, dayOfYear);

        return gc.getTime();
    }

    Date addDays(final Date date, final int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
