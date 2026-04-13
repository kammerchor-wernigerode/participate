package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.filter;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

public class BootstrapChoiceFilter<T> extends ChoiceFilter<T> {

    public BootstrapChoiceFilter(String id, IModel<T> model, FilterForm<?> form,
                                 List<? extends T> choices,
                                 IChoiceRenderer<? super T> renderer, boolean autoSubmit) {
        super(id, model, form, choices, renderer, false);

        DropDownChoice<T> choice = getChoice();
        if (autoSubmit) {
            choice.add(new AjaxFormComponentUpdatingBehavior("change") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.appendJavaScript("""
                        document.getElementById("%s").submit()\
                        """.formatted(form.getMarkupId()));
                }
            });
        }
    }
}
