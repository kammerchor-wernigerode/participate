package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.filter;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.model.IModel;

public class BootstrapTextFilter<T> extends TextFilter<T> {

    public BootstrapTextFilter(String id, IModel<T> model, FilterForm<?> form) {
        super(id, model, form);
    }
}
