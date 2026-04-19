package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.filter;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

public class BootstrapTextFilter<T> extends TextFilter<T> {

    public BootstrapTextFilter(String id, IModel<T> model, FilterForm<?> form) {
        super(id, model, form);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        TextField<T> filter = getFilter();
        Form<?> form = filter.getForm();
        AjaxSubmitLink reset = new AjaxSubmitLink("reset", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                filter.setModelObject(null);
                target.add(form);
            }
        };
        add(reset);
    }
}
