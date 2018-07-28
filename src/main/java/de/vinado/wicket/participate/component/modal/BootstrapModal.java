package de.vinado.wicket.participate.component.modal;

import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.AbstractRepeater;

/**
 * Bootstrap based modal component
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapModal extends Panel {

    /**
     * @param id markup id
     */
    public BootstrapModal(final String id) {
        super(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        final WebMarkupContainer container = new WebMarkupContainer(getModalId());
        container.setOutputMarkupId(true);
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(false);
        add(container);
    }

    /**
     * Sets the content of the modal.
     *
     * @param component {@link org.apache.wicket.Component}
     * @return replaced content of the modal
     */
    public BootstrapModal setContent(final Component component) {
        if (!component.getId().equals(getModalId())) {
            throw new WicketRuntimeException(
                    "Modal content id is wrong. Component id:" + component.getId() + "; content id: " + getModalId());
        } else if (component instanceof AbstractRepeater) {
            throw new WicketRuntimeException(
                    "A repeater component cannot be used as the content of a modal, please use repeater's parent");
        }
        component.setOutputMarkupId(true);
        component.setOutputMarkupPlaceholderTag(true);
        component.setVisible(true);
        addOrReplace(component);
        return this;
    }

    /**
     * A short alias for {@link #appendShowDialogJavaScript}
     *
     * @param target current {@link org.apache.wicket.ajax.AjaxRequestTarget}
     * @return this instance for chaining
     */
    public BootstrapModal show(final AjaxRequestTarget target) {
        target.add(this);

        return appendShowDialogJavaScript(target);
    }

    /**
     * A short alias for {@link BootstrapModal#appendCloseDialogJavaScript}
     *
     * @param target current {@link org.apache.wicket.ajax.AjaxRequestTarget}
     * @return this instance for chaining
     */
    public BootstrapModal close(final AjaxRequestTarget target) {
        return appendCloseDialogJavaScript(target);
    }

    /**
     * Appends modal show event to current {@link org.apache.wicket.ajax.AjaxRequestTarget}.
     *
     * @param target current {@link org.apache.wicket.ajax.AjaxRequestTarget}
     * @return this instance for chaining
     */
    protected BootstrapModal appendShowDialogJavaScript(final AjaxRequestTarget target) {
        target.appendJavaScript(createActionScript(getMarkupId(true), "show"));
        return this;
    }

    /**
     * Appends modal close/hide event to current {@link org.apache.wicket.ajax.AjaxRequestTarget}.
     *
     * @param target current {@link org.apache.wicket.ajax.AjaxRequestTarget}
     * @return this instance for chaining
     */
    protected BootstrapModal appendCloseDialogJavaScript(final AjaxRequestTarget target) {
        target.prependJavaScript(createActionScript(getMarkupId(true), "hide"));
        return this;
    }

    /**
     * Creates an action script to open/close the modal on client side.
     *
     * @param markupId the component's markup id
     * @param action   possible values: show/hide
     * @return script to open/close the modal
     */
    protected String createActionScript(final String markupId, final String action) {
        return "$('#" + markupId + " .modal').modal('" + action + "');";
    }

    /**
     * Executes custom methods after the modal has finished being hidden from the user.
     *
     * @param modal  {@link BootstrapModal}
     * @param target current {@link org.apache.wicket.ajax.AjaxRequestTarget}
     */
    @SuppressWarnings("unused")
    public void onAfterClose(final BootstrapModal modal, final AjaxRequestTarget target) {
    }

    /**
     * @return markup id of the modal
     */
    public String getModalId() {
        return ParticipatePage.MODAL_ID;
    }
}
