package de.vinado.app.participate.wicket;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.RuntimeConfigurationType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("app.wicket")
public class WicketProperties {

    private RuntimeConfigurationType runtimeConfiguration = RuntimeConfigurationType.DEVELOPMENT;
}
