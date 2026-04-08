package de.kammerchorwernigerode.app.participate.wicket.markup.html.image;

import org.apache.wicket.util.io.IClusterable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class IconType implements IClusterable {

    @Getter(AccessLevel.PROTECTED)
    private final String cssClassName;

    public abstract String cssClassName();
}
