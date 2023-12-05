package de.vinado.wicket.bt4.modal;

import de.agilecoders.wicket.jquery.util.Strings2;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.AbstractRepeater;

@Deprecated(forRemoval = true)
public class ModalAnchor extends Panel {

    public static final String MODAL_ID = "modal";

    public ModalAnchor(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupPlaceholderTag(true);

        add(new WebMarkupContainer(getModalId())
            .setOutputMarkupPlaceholderTag(true));
    }

    public ModalAnchor setContent(Component component) {
        if (!component.getId().equals(getModalId())) {
            throw new WicketRuntimeException("Modal content id is wrong. Component id:" + component.getId() + "; content id: " + getModalId());
        } else if (component instanceof AbstractRepeater) {
            throw new WicketRuntimeException("A repeater component cannot be used as the content of a modal, please use repeater's parent");
        }

        addOrReplace(component
            .setOutputMarkupPlaceholderTag(true));
        return this;
    }

    public ModalAnchor show(AjaxRequestTarget target) {
        target.add(this);
        return appendShowDialogJavaScript(target);
    }

    public ModalAnchor close(AjaxRequestTarget target) {
        return appendCloseDialogJavaScript(target);
    }

    protected ModalAnchor appendShowDialogJavaScript(AjaxRequestTarget target) {
        target.appendJavaScript(createActionScript(getMarkupId(true), "show"));
        return this;
    }

    protected ModalAnchor appendCloseDialogJavaScript(AjaxRequestTarget target) {
        target.prependJavaScript(createActionScript(getMarkupId(true), "hide"));
        return this;
    }

    protected String createActionScript(String markupId, String action) {
        return "$('#" + Strings2.escapeMarkupId(markupId) + " .modal').modal('" + action + "');";
    }

    public String getModalId() {
        return MODAL_ID;
    }
}
