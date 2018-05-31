package de.vinado.wicket.participate.data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@DiscriminatorValue("voice")
public class Voice extends ListOfValue implements Configurable {

    public final static String SOPRANO = "SOPRANO";
    public final static String ALTO = "ALTO";
    public final static String TENOR = "TENOR";
    public final static String BASS = "BASS";

    public Voice() {
    }
}
