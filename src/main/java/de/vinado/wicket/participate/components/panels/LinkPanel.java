package de.vinado.wicket.participate.components.panels;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

/**
 * @author Vincent Nadoll
 */
public class LinkPanel extends Panel {

    public LinkPanel(String id, IModel<?> label, SerializableFunction<String, AbstractLink> constructor) {
        super(id, label);

        setRenderBodyOnly(true);

        AbstractLink link = constructor.apply("link");
        link.setBody(label);
        add(link);
    }
}
