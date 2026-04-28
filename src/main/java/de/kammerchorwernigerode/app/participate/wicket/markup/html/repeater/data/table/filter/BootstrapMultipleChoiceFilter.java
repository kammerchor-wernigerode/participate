package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.filter;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.AbstractFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

public class BootstrapMultipleChoiceFilter<T> extends AbstractFilter {

    private final boolean autoSubmit;
    private final FilterForm<?> form;

    @Getter
    private final ListMultipleChoice<T> multipleChoice;

    public BootstrapMultipleChoiceFilter(String id, IModel<? extends Collection<T>> model, FilterForm<?> form,
                                         List<? extends T> choices, IChoiceRenderer<? super T> renderer,
                                         boolean autoSubmit) {
        super(id, form);
        this.autoSubmit = autoSubmit;
        this.form = form;
        this.multipleChoice = new ListMultipleChoice<>("filter", model, choices, renderer);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (autoSubmit) {
            multipleChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    String markupId = form.getMarkupId();
                    target.appendJavaScript("document.getElementById('" + markupId + "').submit()");
                }
            });
        }
        enableFocusTracking(multipleChoice);
        add(multipleChoice);
    }
}
