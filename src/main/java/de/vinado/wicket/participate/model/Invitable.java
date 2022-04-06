package de.vinado.wicket.participate.model;

/**
 * @author Vincent Nadoll
 */
@FunctionalInterface
public interface Invitable {

    InvitationStatus getInvitationStatus();
}
