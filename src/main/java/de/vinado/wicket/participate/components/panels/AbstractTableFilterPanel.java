package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.agilecoders.wicket.jquery.util.Strings2;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapInlineFormDecorator;
import de.vinado.wicket.participate.model.filters.IFilterPanel;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.visit.IVisitor;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AbstractTableFilterPanel<T, F> extends Panel implements IFilterPanel<F> {

    protected Form<T> form;
    protected WebMarkupContainer inner;

    private IModel<List<T>> model;

    private BootstrapAjaxLink<Void> showButton;
    private boolean visible = false;

    public AbstractTableFilterPanel(final String id, final IModel<List<T>> model, final IModel<F> filterModel) {
        super(id, filterModel);

        this.model = model;

        form = new Form<>("form");

        inner = new WebMarkupContainer("wmc") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (visible) {
                    add(new CssClassNameAppender("in"));
                }
            }
        };
        inner.setOutputMarkupPlaceholderTag(true);

        showButton = new BootstrapAjaxLink<Void>("showBtn", Buttons.Type.Default) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                visible = !visible;
                setLabel(visible
                    ? new ResourceModel("filter.hide", "Hide Filter")
                    : new ResourceModel("filter.enable", "Show Filter"));

                if (!visible) {
                    onReset(target);
                }

                target.add(showButton);
                target.focusComponent(getFocusableFormComponent());
            }
        };
        showButton.setOutputMarkupId(true);
        showButton.add(new AttributeModifier("data-parent", "#" + Strings2.getMarkupId(AbstractTableFilterPanel.this)));
        showButton.add(new AttributeModifier("href", "#" + inner.getMarkupId(true)));
        showButton.setSize(Buttons.Size.Small);
        showButton.setIconType(FontAwesome5IconType.filter_s);
        showButton.setLabel(new ResourceModel("filter.enable", "Show Filter"));
        form.add(showButton);

        final BootstrapAjaxButton filterBtn = new BootstrapAjaxButton("filterBtn", Model.of(""), form, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target) {
                onSearch(target, filterModel.getObject());
                target.focusComponent(getFocusableFormComponent());
            }
        };
        filterBtn.setIconType(FontAwesome5IconType.search_s);
        filterBtn.add(new TooltipBehavior(new ResourceModel("filter", "Filter")));
        inner.add(filterBtn);

        final BootstrapAjaxButton resetBtn = new BootstrapAjaxButton("resetBtn", Model.of(""), form, Buttons.Type.Default) {
            @Override
            @SuppressWarnings("unchecked")
            protected void onSubmit(final AjaxRequestTarget target) {
                filterModel.setObject(newFilter((Class<F>) filterModel.getObject().getClass()));
                target.add(form);
                onReset(target);
                target.focusComponent(getFocusableFormComponent());
            }
        };
        resetBtn.setIconType(FontAwesome5IconType.undo_s);
        resetBtn.add(new TooltipBehavior(new ResourceModel("reset", "Reset")));
        inner.add(resetBtn);

        add(form);

        form.setDefaultButton(filterBtn);
        form.add(inner);
    }

    protected Component getFocusableFormComponent() {
        return null;
    }

    public void addBootstrapFormDecorator(final Form<T> form) {
        form.add(new AttributeModifier("class", "form-inline clearfix"));
        form.visitChildren(FormComponent.class, (IVisitor<FormComponent<?>, Void>) (component, voidIVisit) -> {
            if (!(component instanceof Button)) {
                component.add(BootstrapInlineFormDecorator.decorate());
            }
            voidIVisit.dontGoDeeper();
        });
    }

    private F newFilter(Class<F> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onSearch(final AjaxRequestTarget target, final F filter) {
        getDataProvider().set(getFilteredData(filter));
        getDataTable().setCurrentPage(0L);
        target.add(getDataTable());
    }

    @Override
    public void onReset(final AjaxRequestTarget target) {
        model.setObject(getDefaultData());
        getDataProvider().set(model.getObject());
        getDataTable().setCurrentPage(0L);
        target.add(getDataTable());
    }

    public abstract List<T> getFilteredData(final F filter);

    public abstract List<T> getDefaultData();

    public abstract SimpleDataProvider<T, ?> getDataProvider();

    public abstract DataTable<T, ?> getDataTable();
}
