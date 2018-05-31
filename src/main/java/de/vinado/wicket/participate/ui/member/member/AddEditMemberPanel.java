package de.vinado.wicket.participate.ui.member.member;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.component.provider.Select2GroupProvider;
import de.vinado.wicket.participate.component.validator.ConditionalValidator;
import de.vinado.wicket.participate.data.Configurable;
import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dto.MemberDTO;
import de.vinado.wicket.participate.event.MemberUpdateEvent;
import de.vinado.wicket.participate.service.ListOfValueService;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2MultiChoice;

/**
 * Panel for administration of {@link de.vinado.wicket.participate.data.Member Members}
 *
 * @author Julius Felchow (julius.felchow@gmail.com)
 */
public class AddEditMemberPanel extends BootstrapModalPanel<MemberDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    @SpringBean
    @SuppressWarnings("unused")
    private ListOfValueService listOfValueService;

    private boolean edit;
    private boolean remove = false;

    /**
     * @param modal {@link de.vinado.wicket.participate.component.modal.BootstrapModal}
     * @param model IModel of {@link de.vinado.wicket.participate.data.dto.MemberDTO}
     */
    public AddEditMemberPanel(final BootstrapModal modal, final IModel<String> title, IModel<MemberDTO> model) {
        super(modal, title, model);
        edit = null != model.getObject().getMember();

        final TextField<String> firstNameTf = new TextField<>("firstName");
        firstNameTf.setLabel(new ResourceModel("firstName", "Given name"));
        firstNameTf.setRequired(true);
        inner.add(firstNameTf);

        final TextField<String> lastNameTf = new TextField<>("lastName");
        lastNameTf.setLabel(new ResourceModel("lastName", "Surname"));
        lastNameTf.setRequired(true);
        inner.add(lastNameTf);

        final EmailTextField emailTf = new EmailTextField("email");
        emailTf.setLabel(new ResourceModel("email", "Email"));
        emailTf.setRequired(true);
        emailTf.add(new ConditionalValidator<String>(new ResourceModel("uniqueEmail", "Email address has to be unique")) {
            @Override
            public boolean getCondition(final String value) {
                if (Strings.isEmpty(model.getObject().getEmail())) {
                    return false;
                } else if (value.equals(model.getObject().getEmail())) {
                    return false;
                }

                return personService.hasMember(value);
            }
        });
        inner.add(emailTf);

        final DropDownChoice<Configurable> voiceDd = new DropDownChoice<>("voice",
            listOfValueService.getConfigurableList(Voice.class), new ChoiceRenderer<>("name"));
        voiceDd.setLabel(new ResourceModel("voiceGroup", "Voice"));
        inner.add(voiceDd);

        final Select2MultiChoice<Group> groupS2mc = new Select2MultiChoice<Group>("groups",
            new PropertyModel<>(model, "groups"), new Select2GroupProvider(personService));
        groupS2mc.getSettings().setLanguage(getLocale().getLanguage());
        groupS2mc.getSettings().setCloseOnSelect(true);
        groupS2mc.getSettings().setTheme(new Select2BootstrapTheme(true));
        groupS2mc.getSettings().setPlaceholder(new ResourceModel("pleaseChoose", "Please choose").getObject());
        groupS2mc.setLabel(new ResourceModel("selectGroup", "Select group"));
        inner.add(groupS2mc);

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
                    setLabel(new ResourceModel("memberWillBeRemoved", "Member will be removed"));
                    setIconType(FontAwesomeIconType.exclamation_circle);
                } else {
                    setLabel(new ResourceModel("removeMember", "Remove member"));
                    setIconType(FontAwesomeIconType.trash);
                }
                target.add(this);
            }
        };
        removeBtn.setLabel(new ResourceModel("removeMember", "Remove member"));
        removeBtn.setIconType(FontAwesomeIconType.trash);
        removeBtn.setSize(Buttons.Size.Mini);
        removeBtn.setOutputMarkupId(true);
        inner.add(removeBtn);

        addBootstrapHorizontalFormDecorator(inner);
    }

    @Override
    protected void onSaveSubmit(final IModel<MemberDTO> model, final AjaxRequestTarget target) {
        if (edit) {
            if (remove) {
                personService.removeMember(model.getObject().getMember());
                send(getWebPage(), Broadcast.BREADTH, new MemberUpdateEvent(target));
                Snackbar.show(target, new ResourceModel("removeMemberS", "Member removed successfully"));
                return;
            }
            personService.saveMember(model.getObject());
            send(getWebPage(), Broadcast.BREADTH, new MemberUpdateEvent(target));
        } else {
            personService.createMember(model.getObject());
        }
        send(getWebPage(), Broadcast.BREADTH, new MemberUpdateEvent(target));
    }
}
