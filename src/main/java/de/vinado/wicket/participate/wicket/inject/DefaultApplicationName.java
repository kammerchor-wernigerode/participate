package de.vinado.wicket.participate.wicket.inject;

/**
 * @author Vincent Nadoll
 */
public class DefaultApplicationName implements ApplicationName {

    @Override
    public String get() {
        return "Participate";
    }
}
