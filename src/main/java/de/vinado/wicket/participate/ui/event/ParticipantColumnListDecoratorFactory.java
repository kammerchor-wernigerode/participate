package de.vinado.wicket.participate.ui.event;

/**
 * @author Vincent Nadoll
 */
@FunctionalInterface
public interface ParticipantColumnListDecoratorFactory {

    ParticipantColumnList decorate(ParticipantColumnList preset);
}
