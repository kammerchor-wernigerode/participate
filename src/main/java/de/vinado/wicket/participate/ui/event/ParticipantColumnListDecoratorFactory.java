package de.vinado.wicket.participate.ui.event;

@FunctionalInterface
public interface ParticipantColumnListDecoratorFactory {

    ParticipantColumnList decorate(ParticipantColumnList preset);
}
