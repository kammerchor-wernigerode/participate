package de.vinado.wicket.participate.data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@DiscriminatorValue("communicationType")
public class CommunicationType extends ListOfValue implements Configurable {

    public final static String TELEPHONE = "TELEPHONE";
    public final static String MOBILE_PHONE = "MOBILE_PHONE";
    public final static String EMAIL = "EMAIL";

    public CommunicationType() {
    }
}
