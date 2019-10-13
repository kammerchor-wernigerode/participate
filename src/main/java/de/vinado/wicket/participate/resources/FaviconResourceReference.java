package de.vinado.wicket.participate.resources;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author Vincent Nadoll
 */
public class FaviconResourceReference extends PackageResourceReference {

    public static final FaviconResourceReference INSTANCE = new FaviconResourceReference();

    private FaviconResourceReference() {
        super(FaviconResourceReference.class, "favicon.ico");
    }
}
