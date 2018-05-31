package de.vinado.wicket.participate.data;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface Attributable {

    Class getAttributeMappingClass();

    Object addAttributeForObject(Attribute attribute);
}
