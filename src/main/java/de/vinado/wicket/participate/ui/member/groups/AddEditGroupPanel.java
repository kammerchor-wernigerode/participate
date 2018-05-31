package de.vinado.wicket.participate.ui.member.groups;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.behavoir.AutosizeBehavior;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.dto.GroupDTO;
import de.vinado.wicket.participate.event.GroupUpdateEvent;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AddEditGroupPanel extends BootstrapModalPanel<GroupDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private boolean edit;
    private boolean remove = false;

    public AddEditGroupPanel(final BootstrapModal modal, final IModel<String> title, final IModel<GroupDTO> model) {
        super(modal, title, model);

        this.edit = null != model.getObject().getGroup();

        final TextField nameTf = new TextField("name");
        nameTf.setRequired(true);
        inner.add(nameTf);

        final TextArea descriptionTa = new TextArea("description");
        descriptionTa.add(new AutosizeBehavior());
        inner.add(descriptionTa);

        final DateTextFieldConfig dtfConfig = new DateTextFieldConfig();
        dtfConfig.withLanguage(getLocale().getLanguage());
        dtfConfig.withFormat("dd.MM.yyyy");
        dtfConfig.withStartDate(new DateTime());
        dtfConfig.autoClose(true);

        final DateTextField validUntilDtf = new DateTextField("validUntil", dtfConfig);
        validUntilDtf.setLabel(new ResourceModel("dateOfExpiry", "Date of Expiry"));
        inner.add(validUntilDtf);

        final BootstrapAjaxLink removeBtn = new BootstrapAjaxLink("removeBtn", Buttons.Type.Link) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                setVisible(edit);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
                remove = !remove;
                if (remove) {
                    setLabel(new ResourceModel("groupWillBeRemoved", "Group will be removed"));
                    setIconType(FontAwesomeIconType.exclamation_circle);
                } else {
                    setLabel(new ResourceModel("removeGroup", "Remove group"));
                    setIconType(FontAwesomeIconType.trash);
                }
                target.add(this);
            }
        };
        removeBtn.setLabel(new ResourceModel("removeGroup", "Remove group"));
        removeBtn.setIconType(FontAwesomeIconType.trash);
        removeBtn.setSize(Buttons.Size.Mini);
        removeBtn.setOutputMarkupId(true);
        inner.add(removeBtn);

        addBootstrapHorizontalFormDecorator(inner);
    }

    @Override
    protected void onSaveSubmit(final IModel<GroupDTO> model, final AjaxRequestTarget target) {
        if (edit) {
            if (remove) {
                personService.removeGroup(model.getObject().getGroup());
                send(getWebPage(), Broadcast.BREADTH, new GroupUpdateEvent(target));
                Snackbar.show(target, new ResourceModel("removeGroupS"));
                return;
            }
            onUpdate(personService.saveGroup(model.getObject()), target);
        } else {
            onUpdate(personService.createGroup(model.getObject()), target);
        }
    }

    protected abstract void onUpdate(final Group savedGroup, final AjaxRequestTarget target);
}
