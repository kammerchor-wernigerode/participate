package de.vinado.wicket.tabs;

import lombok.NonNull;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

public class LambdaTab extends AbstractTab {

    private static final long serialVersionUID = -4606691917286352701L;

    private final SerializableFunction<String, WebMarkupContainer> componentConstructor;

    public LambdaTab(@NonNull IModel<String> title,
                     @NonNull SerializableFunction<String, WebMarkupContainer> componentConstructor) {
        super(title);
        this.componentConstructor = componentConstructor;
    }

    @Override
    public WebMarkupContainer getPanel(String id) {
        return componentConstructor.apply(id);
    }
}
