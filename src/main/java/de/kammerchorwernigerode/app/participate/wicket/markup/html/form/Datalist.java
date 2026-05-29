package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Components;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

public class Datalist<T> extends Panel {

    private final FormComponent<T> component;
    private final IModel<? extends List<T>> choices;
    private final IChoiceRenderer<T> renderer;

    public Datalist(String id, FormComponent<T> component, IModel<? extends List<T>> choices) {
        this(id, component, choices, new ChoiceRenderer<>());
    }

    public Datalist(String id, FormComponent<T> component, IModel<? extends List<T>> choices,
                    IChoiceRenderer<T> renderer) {
        super(id);
        this.component = component;
        this.choices = wrap(choices);
        this.renderer = renderer;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        Options options = new Options("options", choices, renderer);
        add(options);

        component.setOutputMarkupId(true);
        component.add(AttributeModifier.replace("list", getMarkupId()));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        Components.assertTag(this, tag, "datalist");
    }


    private class Options extends ListView<T> {

        private final IChoiceRenderer<T> renderer;

        public Options(String id, IModel<? extends List<T>> model, IChoiceRenderer<T> renderer) {
            super(id, model);
            this.renderer = renderer;
        }

        @Override
        protected void populateItem(ListItem<T> item) {
            IModel<T> model = item.getModel();

            IModel<Object> displayValue = model.map(renderer::getDisplayValue);
            item.add(AttributeModifier.replace("value", displayValue));

            IModel<String> idValue = model.map(object -> renderer.getIdValue(object, item.getIndex()));
            item.add(AttributeModifier.replace("data-datalist-id", idValue));
        }
    }
}
