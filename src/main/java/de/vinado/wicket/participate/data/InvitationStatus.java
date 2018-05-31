package de.vinado.wicket.participate.data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@DiscriminatorValue("invitationStatus")
public class InvitationStatus extends ListOfValue implements Configurable {

    public final static String PENDING = "PENDING";
    public final static String ACCEPTED = "ACCEPTED";
    public final static String DECLINED = "DECLINED";
    
    public InvitationStatus() {
    }
}
