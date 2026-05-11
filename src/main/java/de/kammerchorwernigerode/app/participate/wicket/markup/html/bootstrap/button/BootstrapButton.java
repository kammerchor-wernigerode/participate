package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Size;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import org.apache.wicket.Component;

public interface BootstrapButton<T extends Component> {

    T setVariant(Variant variant);

    T setSize(Size size);
}
