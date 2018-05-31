package de.vinado.wicket.participate.data;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface Communicatable {

    Class getCommunicationMappingClass();

    Object addCommunicationForObject(Communication communication);
}
