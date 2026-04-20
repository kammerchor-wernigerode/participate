package de.kammerchorwernigerode.app.participate.event.presentation.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvitationStatusSelection implements Serializable {

    private boolean uninvited;
    private boolean tentative;
    private boolean accepted;
    private boolean declined;
    private boolean pending;
}
