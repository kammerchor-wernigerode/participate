package de.vinado.wicket.participate.ui.singers;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.tooltip.TooltipBehavior;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.AbstractSubmitLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;

import java.util.Arrays;

public abstract class SingerFilterForm extends GenericPanel<SingerFilter> {

    public SingerFilterForm(String id, IModel<SingerFilter> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        queue(form("form"));
        queue(searchTerm("searchTerm"));
        queue(voice("voice"));
        queue(showAll("showAll"));

        queue(resetButton("reset"));
        queue(applyButton("apply"));
    }

    protected Form<SingerFilter> form(String wicketId) {
        return new Form<>(wicketId);
    }

    protected MarkupContainer searchTerm(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<String> model = LambdaModel.of(getModel(), SingerFilter::getSearchTerm, SingerFilter::setSearchTerm);
        FormComponent<String> control = new TextField<>("control", model)
            .setLabel(new ResourceModel("search", "Search"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer voice(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Voice> model = LambdaModel.of(getModel(), SingerFilter::getVoice, SingerFilter::setVoice);
        FormComponent<Voice> control = new DropDownChoice<>("control", model, Arrays.asList(Voice.values()),
            new EnumChoiceRenderer<>())
            .setLabel(new ResourceModel("voice", "Voice"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer showAll(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Boolean> model = LambdaModel.of(getModel(), SingerFilter::isShowAll, SingerFilter::setShowAll);
        FormComponent<Boolean> control = new CheckBox("control", model)
            .setLabel(new ResourceModel("showAll", "Show All"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected AbstractLink resetButton(String id) {
        BootstrapAjaxLink<Void> button = new BootstrapAjaxLink<>(id, Buttons.Type.Outline_Secondary) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                resetFilter();
                target.add(SingerFilterForm.this);
                onReset();
            }
        };
        button.setIconType(FontAwesome5IconType.undo_s);
        button.add(new TooltipBehavior(new ResourceModel("reset", "Reset")));
        return button;
    }

    private void resetFilter() {
        setModelObject(new SingerFilter());
    }

    private AbstractSubmitLink applyButton(String id) {
        AjaxSubmitLink button = new AjaxSubmitLink(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(SingerFilterForm.this);
                onApply();
            }
        };
        button.add(new ButtonBehavior(Buttons.Type.Primary));
        button.add(new Icon("icon", FontAwesome5IconType.filter_s));
        button.add(new TooltipBehavior(new ResourceModel("filter", "Filter")));
        return button;
    }

    protected abstract void onApply();

    protected void onReset() {
        onApply();
    }
}
