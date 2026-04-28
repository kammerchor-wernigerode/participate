package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.presentation.model.InvitationStatusSelection;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.AbstractFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;

public class InvitationStatusFilter extends AbstractFilter
    implements IGenericComponent<InvitationStatusSelection, InvitationStatusFilter> {

    private final FilterForm<?> form;

    public InvitationStatusFilter(String id, IModel<InvitationStatusSelection> model, FilterForm<?> form) {
        super(id, form);
        this.form = form;
        setModel(model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<InvitationStatusSelection> model = getModel();

        IModel<Boolean> uninvitedModel =
            LambdaModel.of(model, InvitationStatusSelection::isUninvited, InvitationStatusSelection::setUninvited);
        CheckBox uninvited = new CheckBox("uninvited", uninvitedModel);
        FormComponentLabel uninvitedLabel = new FormComponentLabel("uninvitedLabel", uninvited);
        uninvitedLabel.add(new TooltipBehavior(new ResourceModel("InvitationStatus.UNINVITED")));
        add(uninvited, uninvitedLabel);


        IModel<Boolean> tentativeModel =
            LambdaModel.of(model, InvitationStatusSelection::isTentative, InvitationStatusSelection::setTentative);
        CheckBox tentative = new CheckBox("tentative", tentativeModel);
        FormComponentLabel tentativeLabel = new FormComponentLabel("tentativeLabel", tentative);
        tentativeLabel.add(new TooltipBehavior(new ResourceModel("InvitationStatus.TENTATIVE")));
        add(tentative, tentativeLabel);


        TransparentWebMarkupContainer btnGroup1 = new TransparentWebMarkupContainer("btnGroup1");
        btnGroup1.add(new SqueezeBehavior());
        add(btnGroup1);

        IModel<Boolean> acceptedModel =
            LambdaModel.of(model, InvitationStatusSelection::isAccepted, InvitationStatusSelection::setAccepted);
        CheckBox accepted = new CheckBox("accepted", acceptedModel);
        FormComponentLabel acceptedLabel = new FormComponentLabel("acceptedLabel", accepted);
        acceptedLabel.add(new TooltipBehavior(new ResourceModel("InvitationStatus.ACCEPTED")));
        add(accepted, acceptedLabel);


        IModel<Boolean> declinedModel =
            LambdaModel.of(model, InvitationStatusSelection::isDeclined, InvitationStatusSelection::setDeclined);
        CheckBox declined = new CheckBox("declined", declinedModel);
        FormComponentLabel declinedLabel = new FormComponentLabel("declinedLabel", declined);
        declinedLabel.add(new TooltipBehavior(new ResourceModel("InvitationStatus.DECLINED")));
        add(declined, declinedLabel);


        TransparentWebMarkupContainer btnGroup2 = new TransparentWebMarkupContainer("btnGroup2");
        btnGroup2.add(new SqueezeBehavior());
        add(btnGroup2);

        IModel<Boolean> pendingModel =
            LambdaModel.of(model, InvitationStatusSelection::isPending, InvitationStatusSelection::setPending);
        CheckBox pending = new CheckBox("pending", pendingModel);
        FormComponentLabel pendingLabel = new FormComponentLabel("pendingLabel", pending);
        pendingLabel.add(new TooltipBehavior(new ResourceModel("InvitationStatus.PENDING")));
        add(pending, pendingLabel);

        AjaxSubmitLink reset = new AjaxSubmitLink("reset", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                InvitationStatusSelection selection = model.getObject();
                selection.setUninvited(false);
                selection.setTentative(false);
                selection.setAccepted(false);
                selection.setDeclined(false);
                selection.setPending(false);

                target.add(form);
            }
        };
        add(reset);


        form.visitChildren(CheckBox.class, (component, visit) -> {
            if (component instanceof CheckBox checkbox) {
                enableFocusTracking(checkbox);
                checkbox.add(new AjaxFormComponentUpdatingBehavior("change") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        String javaScript = """
                            document.getElementById("%s").submit()\
                            """.formatted(form.getMarkupId());
                        target.appendJavaScript(javaScript);
                    }
                });
            }
        });
    }


    private static class SqueezeBehavior extends Behavior {

        @Override
        public void bind(Component component) {
            component.setOutputMarkupId(true);
        }

        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            String markupId = component.getMarkupId();
            response.render(CssContentHeaderItem.forCSS("""
                #%s {
                    margin-top: -1px;
                }\
                """.formatted(markupId), markupId + "-style"));
        }
    }
}
