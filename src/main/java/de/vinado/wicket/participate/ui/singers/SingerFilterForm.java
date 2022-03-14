package de.vinado.wicket.participate.ui.singers;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.tooltip.TooltipBehavior;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapInlineFormDecorator;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.AbstractSubmitLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.visit.IVisitor;

import java.util.Arrays;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class SingerFilterForm extends Form<SingerFilter> {

    private static final String TAG_NAME = "singerFilterForm";

    static {
        WicketTagIdentifier.registerWellKnownTagName(TAG_NAME);
    }

    public SingerFilterForm(String id, IModel<SingerFilter> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        add(termInput("searchTerm"));
        add(voiceSelect("voice"));
        add(showAllCheck("showAll"));

        add(resetButton("reset"));
        add(applyButton("apply"));

        add(new CssClassNameAppender("form-inline"));
        visitChildren(FormComponent.class, (IVisitor<FormComponent<?>, Void>) (component, visit) -> {
            if (!(component instanceof Button)) {
                component.add(BootstrapInlineFormDecorator.decorate());
            }
            visit.dontGoDeeper();
        });
    }

    protected FormComponent<String> termInput(String id) {
        return new TextField<String>(id)
            .setLabel(new ResourceModel("search", "Search"));
    }

    protected FormComponent<Voice> voiceSelect(String id) {
        DropDownChoice<Voice> select = new DropDownChoice<>(id, Arrays.asList(Voice.values()),
            new EnumChoiceRenderer<>());
        select.setLabel(new ResourceModel("voice", "Voice"));
        return select;
    }

    protected FormComponent<Boolean> showAllCheck(String id) {
        return new CheckBox(id);
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

    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(TAG_NAME, false);
    }
}
