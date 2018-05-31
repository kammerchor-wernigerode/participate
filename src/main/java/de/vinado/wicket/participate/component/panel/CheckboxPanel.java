package de.vinado.wicket.participate.component.panel;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class CheckboxPanel<T> extends Panel {

    private IModel<T> model;

    public CheckboxPanel(final String id, final IModel<T> model) {
        super(id, model);
        this.model = model;

        add(new CheckBox("checkBox"));
    }

    public T getValue() {
        return model.getObject();
    }

    public IModel<T> getModel() {
        return model;
    }
}
