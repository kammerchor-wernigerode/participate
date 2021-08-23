package de.vinado.wicket.participate.ui.pages;

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
