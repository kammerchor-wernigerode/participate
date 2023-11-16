package de.vinado.wicket.participate.wicket.common;

import lombok.Value;
import org.apache.wicket.Page;

@Value
public class PageRegistrar {
    String path;
    Class<? extends Page> pageClass;
}
