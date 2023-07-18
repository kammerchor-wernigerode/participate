package de.vinado.wicket.participate.model.dtos;

import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Invitable;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Singer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ParticipantDTO implements Serializable, Invitable {

    private Participant participant;
    private Event event;
    private Singer singer;
    private String token;
    private Date fromDate;
    private Date toDate;
    private InvitationStatus invitationStatus;
    private boolean catering;
    private Accommodation accommodation;
    private boolean car;
    private short carSeatCount = 0;
    private String comment;

    public ParticipantDTO(final Participant participant) {
        this.participant = participant;
        this.event = participant.getEvent();
        this.singer = participant.getSinger();
        this.token = participant.getToken();
        this.fromDate = participant.getFromDate();
        this.toDate = participant.getToDate();
        this.invitationStatus = participant.getInvitationStatus();
        this.catering = participant.isCatering();
        this.accommodation = participant.accommodation();
        this.car = participant.getCarSeatCount() >= 0;
        this.carSeatCount = participant.getCarSeatCount() < 0 ? 0 : participant.getCarSeatCount();
        this.comment = participant.getComment();
    }
}
