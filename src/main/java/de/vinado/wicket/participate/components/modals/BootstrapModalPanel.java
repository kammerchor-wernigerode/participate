package de.vinado.wicket.participate.components.modals;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapFormDecorator;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapInlineFormDecorator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class BootstrapModalPanel<T> extends Panel {

    protected final Form<T> inner;

    private static final String SAVE_BUTTON_ID = "saveButton";

    private static final String CANCEL_BUTTON_ID = "cancelButton";

    private final WebMarkupContainer dialogWmc;

    /**
     * @param modal {@link de.vinado.wicket.participate.components.modals.BootstrapModal}
     * @param title Title model
     * @param model Model
     */
    public BootstrapModalPanel(final BootstrapModal modal, final IModel<String> title, final IModel<T> model) {
        super(modal.getModalId(), model);

        // dialog
        dialogWmc = new WebMarkupContainer("dialog");
        dialogWmc.setOutputMarkupId(true);
        add(dialogWmc);

        // header
        final WebMarkupContainer headerWmc = new WebMarkupContainer("header");
        dialogWmc.add(headerWmc);

        // header title
        final Label titleLabel = new Label("title", null != title ? title.getObject() : "");
        titleLabel.setEscapeModelStrings(true);
        headerWmc.add(titleLabel);

        // form
        inner = new Form<>("form", new CompoundPropertyModel<>(model));
        inner.setOutputMarkupId(true);
        dialogWmc.add(inner);

        // feedback Panel
        final NotificationPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        inner.add(feedback);

        // footer
        final WebMarkupContainer footerWmc = new WebMarkupContainer("footer");
        inner.add(footerWmc);

        // save button
        final BootstrapAjaxButton saveBtn = new BootstrapAjaxButton(SAVE_BUTTON_ID, inner, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target) {
                onSaveSubmit((IModel<T>) getForm().getModel(), target);
                modal.close(target);
                onAfterClose(model, modal, target);
            }

            @Override
            protected void onError(final AjaxRequestTarget target) {
                target.add(feedback);

                getForm().visitFormComponents(new IVisitor<FormComponent<?>, Object>() {
                    @Override
                    public void component(final FormComponent<?> components, final IVisit<Object> iVisit) {
                        if (!components.getRenderBodyOnly()) {
                            target.add(components);
                        }
                    }
                });
            }
        };
        saveBtn.setSize(Buttons.Size.Small);
        saveBtn.setLabel(getSubmitBtnLabel());
        footerWmc.add(saveBtn);

        // cancel button
        final AjaxButton cancelBtn = new AjaxButton(CANCEL_BUTTON_ID) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target) {
                onCancelSubmit(target);
                modal.close(target);
            }
        };
        cancelBtn.setDefaultFormProcessing(false);
        footerWmc.add(cancelBtn);
    }

    protected IModel<String> getSubmitBtnLabel() {
        return new ResourceModel("save", "Save");
    }

    /**
     * Decorates all components inside the form with basic form decorator.
     * <code>
     * <div class="form-group">
     * <label for="inputId">Label</label>
     * <input type="text" id="inputId" class="form-control"/>
     * </div>
     * </code>
     *
     * @param form {@link org.apache.wicket.markup.html.form.Form}
     * @see <a href="http://getbootstrap.com/css/#forms-example">Basic example</a>
     * @see BootstrapFormDecorator#decorate()
     */
    @SuppressWarnings("unused")
    protected void addBootstrapFormDecorator(final Form<T> form) {
        form.visitChildren(FormComponent.class, (IVisitor<FormComponent, Void>) (component, voidIVisit) -> {
            if (!(component instanceof Button) && !(component instanceof CheckGroup) && !(component instanceof RadioGroup)) {
                component.add(BootstrapFormDecorator.decorate());
            }
            voidIVisit.dontGoDeeper();
        });
    }

    /**
     * Decorates all components inside the form with inline form decorator.
     * <code>
     * <div class="form-group">
     * <label for="inputId">Label</label>
     * <input type="text" id="inputId" class="form-control"/>
     * </div>
     * </code>
     *
     * @param form {@link org.apache.wicket.markup.html.form.Form}
     * @see <a href="http://getbootstrap.com/css/#forms-horizontal">Horizontal form</a>
     * @see BootstrapInlineFormDecorator
     */
    @SuppressWarnings("unused")
    protected void addBootstrapInlineFormDecorator(final Form<T> form) {
        form.add(new AttributeModifier("class", "form-inline"));
        form.visitChildren(FormComponent.class, (IVisitor<FormComponent, Void>) (component, voidIVisit) -> {
            if (!(component instanceof Button) && !(component instanceof CheckGroup) && !(component instanceof RadioGroup)) {
                component.add(BootstrapInlineFormDecorator.decorate());
            }
            voidIVisit.dontGoDeeper();
        });
    }

    /**
     * Decorates all components inside the form with modal specific form decorator. Use this method only if your form
     * has a class 'form-horizontal'.
     * <code>
     * <div id="componentId" class="form-group form-group-sm">
     * <label class="col-sm-4 col-md-4 control-label" for="inputId">Label</label>
     * <div class="col-sm-7 col-md-6">
     * <input type="text" id="inputId" class="form-control"/>
     * </div>
     * </div>
     * </code>
     *
     * @param form {@link org.apache.wicket.markup.html.form.Form}
     * @see <a href="http://getbootstrap.com/css/#forms-horizontal">Horizontal form</a>
     * @see BootstrapHorizontalFormDecorator#decorate()
     */
    @SuppressWarnings("unused")
    protected void addBootstrapHorizontalFormDecorator(final Form<T> form) {
        form.add(new AttributeModifier("class", "form-horizontal"));
        form.visitChildren(FormComponent.class, (IVisitor<FormComponent, Void>) (component, voidIVisit) -> {
            if (!(component instanceof Button)) {
                component.add(BootstrapHorizontalFormDecorator.decorate());
            }
            voidIVisit.dontGoDeeper();
        });
    }

    protected abstract void onSaveSubmit(final IModel<T> model, final AjaxRequestTarget target);

    protected void onCancelSubmit(final AjaxRequestTarget target) {
    }

    /**
     * Executes custom methods after the modal has finished being hidden from the user.
     *
     * @param model  Model
     * @param modal  {@link BootstrapModal}
     * @param target {@link org.apache.wicket.ajax.AjaxRequestTarget}
     */
    @SuppressWarnings("unused")
    protected void onAfterClose(final IModel<T> model, final BootstrapModal modal, final AjaxRequestTarget target) {
        modal.onAfterClose(modal, target);
    }

    public enum ModalSize implements ICssClassNameProvider {
        Default(""),
        Small("modal-sm"),
        Medium("modal-md"),
        Large("modal-lg");

        private String cssClassName;

        ModalSize(final String cssClassName) {
            this.cssClassName = cssClassName;
        }

        @Override
        public String cssClassName() {
            return cssClassName;
        }
    }

    /**
     * Sets the modal size initially only. Note that the css class is set but the markup container is not updated. To
     * set the size and update the markup container dynamically use the {@link #setModalSize(de.vinado.wicket.participate.components.modals.BootstrapModalPanel.ModalSize,
     * org.apache.wicket.ajax.AjaxRequestTarget)}
     */
    protected void setModalSize(final ModalSize modalSize) {
        dialogWmc.add(new AttributeModifier("class", "modal-dialog " + modalSize.cssClassName()));
    }

    /**
     * Sets and updates the modal size.
     */
    protected void setModalSize(final ModalSize modalSize, final AjaxRequestTarget target) {
        target.add(dialogWmc.add(new AttributeModifier("class", "modal-dialog " + modalSize.cssClassName())));
    }
}
