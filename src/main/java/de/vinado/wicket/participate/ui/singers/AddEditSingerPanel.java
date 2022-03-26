package de.vinado.wicket.participate.ui.singers;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.modal.FormModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.form.ConditionalValidator;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.events.SingerUpdateEvent;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.Collections;

/**
 * Panel for administration of {@link Singer}s
 *
 * @author Julius Felchow (julius.felchow@gmail.com)
 */
public class AddEditSingerPanel extends FormModal<SingerDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    private final boolean edit;
    private boolean remove = false;

    public AddEditSingerPanel(ModalAnchor anchor, IModel<String> title, IModel<SingerDTO> model) {
        super(anchor, model);

        title(title);

        edit = null != model.getObject().getSinger();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final TextField<String> firstNameTf = new TextField<>("firstName");
        firstNameTf.setLabel(new ResourceModel("firstName", "Given name"));
        firstNameTf.setRequired(true);
        form.add(firstNameTf);

        final TextField<String> lastNameTf = new TextField<>("lastName");
        lastNameTf.setLabel(new ResourceModel("lastName", "Surname"));
        lastNameTf.setRequired(true);
        form.add(lastNameTf);

        final EmailTextField emailTf = new EmailTextField("email");
        emailTf.setLabel(new ResourceModel("email", "Email"));
        emailTf.setRequired(true);
        emailTf.add(new ConditionalValidator<>(this::ensureUnique,
            new ResourceModel("unique.email", "A person with this e-mail address already exists")));
        form.add(emailTf);

        final DropDownChoice<Voice> voiceDd = new DropDownChoice<>("voice",
            Collections.unmodifiableList(Arrays.asList(Voice.values())), new EnumChoiceRenderer<>());
        voiceDd.setLabel(new ResourceModel("voice", "Voice"));
        form.add(voiceDd);

        final BootstrapAjaxLink<Void> removeBtn = new BootstrapAjaxLink<>("removeBtn", Buttons.Type.Link) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                setVisible(edit);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
                remove = !remove;
                if (remove) {
                    setLabel(new ResourceModel("singer.remove.hint", "Singer will be removed"));
                    setIconType(FontAwesome5IconType.exclamation_circle_s);
                } else {
                    setLabel(new ResourceModel("singer.remove", "Remove Singer"));
                    setIconType(FontAwesome5IconType.trash_s);
                }
                target.add(this);
            }
        };
        removeBtn.setLabel(new ResourceModel("singer.remove", "Remove Singer"));
        removeBtn.setIconType(FontAwesome5IconType.trash_s);
        removeBtn.setSize(Buttons.Size.Small);
        removeBtn.setOutputMarkupId(true);
        form.add(removeBtn);

        addBootstrapHorizontalFormDecorator(form);
    }

    private boolean ensureUnique(String email) {
        return StringUtils.equalsIgnoreCase(email, getModelObject().getEmail()) || !personService.hasSinger(email);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        SingerDTO dto = getModelObject();
        if (edit) {
            if (remove) {
                personService.removeSinger(dto.getSinger());
                send(getWebPage(), Broadcast.BREADTH, new SingerUpdateEvent(target));
                Snackbar.show(target, new ResourceModel("singer.remove.success", "The singer has been removed"));
                return;
            }
            personService.saveSinger(dto);
            send(getWebPage(), Broadcast.BREADTH, new SingerUpdateEvent(target));
        } else {
            final Singer singer = personService.createSinger(dto);
            eventService.getUpcomingEvents().forEach(event -> eventService.createParticipant(event, singer));
        }
        send(getWebPage(), Broadcast.BREADTH, new SingerUpdateEvent(target));
    }
}
