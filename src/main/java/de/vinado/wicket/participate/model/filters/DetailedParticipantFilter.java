package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class DetailedParticipantFilter implements Serializable {

    private String name;
    private String comment;
    private InvitationStatus invitationStatus;
    private Voice voice;
    private Date fromDate;
    private Date toDate;
    private boolean accommodation;
    private boolean catering;
}
