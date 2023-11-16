package de.vinado.wicket.participate.wicket.inject;

import java.net.URL;
import java.util.function.Supplier;

@FunctionalInterface
public interface RequestUrl extends Supplier<URL> {
}
