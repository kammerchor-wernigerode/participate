package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TooltipBehavior extends Behavior {

    private static final String INITIALIZATION_SCRIPT = """
        const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        const tooltipList = [...tooltipTriggerList].map(el => new bootstrap.Tooltip(el))\
        """;

    private final IModel<String> model;

    public TooltipBehavior(String tooltip) {
        this(Model.of(tooltip));
    }

    public boolean hasTooltip() {
        return model
            .filter(title -> !Strings.isEmpty(title))
            .isPresent()
            .getObject();
    }

    @Override
    public void bind(Component component) {
        super.bind(component);

        if (hasTooltip()) {
            component.add(new AttributeModifier("data-bs-toggle", "tooltip"));
        }
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        tag.put("data-bs-title", model.getObject());
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        response.render(OnDomReadyHeaderItem.forScript(INITIALIZATION_SCRIPT));
    }

    @Override
    public void detach(Component component) {
        model.detach();
    }
}
