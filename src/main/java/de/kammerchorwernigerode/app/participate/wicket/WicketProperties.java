package de.kammerchorwernigerode.app.participate.wicket;

import org.apache.wicket.RuntimeConfigurationType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("wicket")
public class WicketProperties {

    private RuntimeConfigurationType runtimeConfiguration;
}
