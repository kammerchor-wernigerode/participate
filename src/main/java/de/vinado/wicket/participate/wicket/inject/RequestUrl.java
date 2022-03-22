package de.vinado.wicket.participate.wicket.inject;

import java.net.URL;
import java.util.function.Supplier;

/**
 * @author Vincent Nadoll
 */
@FunctionalInterface
public interface RequestUrl extends Supplier<URL> {
}