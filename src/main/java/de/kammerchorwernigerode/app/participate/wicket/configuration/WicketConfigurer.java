package de.kammerchorwernigerode.app.participate.wicket.configuration;

import de.kammerchorwernigerode.app.participate.wicket.management.ManagementWicketApplication;

public interface WicketConfigurer {

    default void init(ManagementWicketApplication webApplication) {
    }
}
