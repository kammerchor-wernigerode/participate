package de.vinado.wicket.participate.ui.singers;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.app.participate.wicket.form.FormComponentLabel;
import de.vinado.wicket.form.ConditionalValidator;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class AddEditSingerPanel extends GenericPanel<SingerDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    private final boolean edit;
    private boolean remove = false;

    private final Form<SingerDTO> form;

    public AddEditSingerPanel(String id, IModel<SingerDTO> model) {
        super(id, model);

        edit = null != model.getObject().getSinger();
        this.form = form("form");
    }

    private Form<SingerDTO> form(String wicketId) {
        return new Form<>(wicketId, getModel()) {

            @Override
            protected void onSubmit() {
                super.onSubmit();

                AddEditSingerPanel.this.onSubmit();
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form);

        TextField<String> firstNameTf = new TextField<>("firstName");
        firstNameTf.setLabel(new ResourceModel("firstName", "Given name"));
        firstNameTf.setRequired(true);
        form.add(firstNameTf, new FormComponentLabel("firstNameLabel", firstNameTf));

        TextField<String> lastNameTf = new TextField<>("lastName");
        lastNameTf.setLabel(new ResourceModel("lastName", "Surname"));
        lastNameTf.setRequired(true);
        form.add(lastNameTf, new FormComponentLabel("lastNameLabel", lastNameTf));

        EmailTextField emailTf = new EmailTextField("email");
        emailTf.setLabel(new ResourceModel("email", "Email"));
        emailTf.setRequired(true);
        emailTf.add(new ConditionalValidator<>(this::ensureUnique,
            new ResourceModel("unique.email", "A person with this e-mail address already exists")));
        form.add(emailTf, new FormComponentLabel("emailLabel", emailTf));

        DropDownChoice<Voice> voiceDd = new DropDownChoice<>("voice",
            Collections.unmodifiableList(Arrays.asList(Voice.values())), new EnumChoiceRenderer<>());
        voiceDd.setLabel(new ResourceModel("voice", "Voice"));
        form.add(voiceDd, new FormComponentLabel("voiceLabel", voiceDd));

        BootstrapAjaxLink<Void> removeBtn = new BootstrapAjaxLink<>("removeBtn", Buttons.Type.Link) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                setVisible(edit);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                remove = !remove;
                if (remove) {
                    setLabel(new ResourceModel("singer.remove.hint", "Singer will be removed"));
                    setIconType(FontAwesome6IconType.circle_exclamation_s);
                } else {
                    setLabel(new ResourceModel("singer.remove", "Remove Singer"));
                    setIconType(FontAwesome6IconType.trash_s);
                }
                target.add(this);
            }
        };
        removeBtn.setLabel(new ResourceModel("singer.remove", "Remove Singer"));
        removeBtn.setIconType(FontAwesome6IconType.trash_s);
        removeBtn.setSize(Buttons.Size.Small);
        removeBtn.setOutputMarkupId(true);
        form.add(removeBtn);
    }

    private boolean ensureUnique(String email) {
        return StringUtils.equalsIgnoreCase(email, getModelObject().getEmail()) || !personService.hasSinger(email);
    }

    protected void onSubmit() {
        SingerDTO dto = getModelObject();
        if (edit) {
            if (remove) {
                delete(dto);
                return;
            }
            update(dto);
        } else {
            create(dto);
        }
    }

    private void create(SingerDTO dto) {
        Singer singer = personService.createSinger(dto);
        dto.setSinger(singer);
        eventService.getUpcomingEvents().forEach(event -> eventService.createParticipant(event, singer));
    }

    private void update(SingerDTO dto) {
        personService.saveSinger(dto);
    }

    private void delete(SingerDTO dto) {
        Singer singer = dto.getSinger();
        personService.removeSinger(singer);
        eventService.getUpcomingEvents().stream()
            .map(eventService::getParticipants)
            .flatMap(List::stream)
            .filter(equals(singer))
            .map(ParticipantDTO::new)
            .forEach(eventService::declineEvent);
        singer.setActive(false);
    }

    private static Predicate<Participant> equals(Singer singer) {
        return participant -> Objects.equals(participant.getSinger().getId(), singer.getId());
    }
}
