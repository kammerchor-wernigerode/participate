package de.vinado.wicket.participate.wicket.inject;

public class DefaultApplicationName implements ApplicationName {

    @Override
    public String get() {
        return "Participate";
    }
}
