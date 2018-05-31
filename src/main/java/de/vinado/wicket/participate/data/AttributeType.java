package de.vinado.wicket.participate.data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@DiscriminatorValue("attributeType")
public class AttributeType extends ListOfValue implements Configurable {

    public AttributeType() {
    }

}
