package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.components.panels.AbstractTableFilterPanel;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class ParticipantFilterPanel extends AbstractTableFilterPanel<Participant, ParticipantFilter> {

    private final TextField<String> searchTermTf;

    public ParticipantFilterPanel(String id, IModel<List<Participant>> model,
                                  IModel<ParticipantFilter> filterModel) {
        super(id, model, filterModel);

        searchTermTf = new TextField<>("name");
        searchTermTf.setLabel(new ResourceModel("search", "Search"));
        inner.add(searchTermTf);

        DropDownChoice<InvitationStatus> invitationStatusDdc = new DropDownChoice<>("invitationStatus",
            Collections.unmodifiableList(Arrays.asList(InvitationStatus.values())), new EnumChoiceRenderer<>());
        invitationStatusDdc.setLabel(new ResourceModel("invitationStatus", "Invitation Status"));
        inner.add(invitationStatusDdc);

        DropDownChoice<Voice> voiceDdc = new DropDownChoice<>("voice",
            Collections.unmodifiableList(Arrays.asList(Voice.values())), new EnumChoiceRenderer<>());
        voiceDdc.setLabel(new ResourceModel("voice", "Voice"));
        inner.add(voiceDdc);

        addBootstrapFormDecorator(form);
    }

    @Override
    protected Component getFocusableFormComponent() {
        return searchTermTf;
    }
}
