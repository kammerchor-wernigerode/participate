package de.vinado.wicket.participate.wicket.common;

import lombok.Value;
import org.apache.wicket.Page;

/**
 * @author Vincent Nadoll
 */
@Value
public class PageRegistrar {
    String path;
    Class<? extends Page> pageClass;
}
