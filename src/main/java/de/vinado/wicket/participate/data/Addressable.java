package de.vinado.wicket.participate.data;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface Addressable {

    Class getAddressMappingClass();

    Object addAddressForObject(Address address);
}
