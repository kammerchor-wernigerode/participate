package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.panels.IconPanel;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.ArrayList;
import java.util.List;

public class BasicParticipantColumnPreset extends ParticipantColumnListDecorator implements ParticipantColumnList {

    public BasicParticipantColumnPreset() {
        super(columns());
    }

    private static List<IColumn<Participant, SerializableFunction<Participant, ?>>> columns() {
        ArrayList<IColumn<Participant, SerializableFunction<Participant, ?>>> columns = new ArrayList<>();
        columns.add(invitationStatusColumn());
        columns.add(nameColumn());
        columns.add(voiceColumn());
        return columns;
    }

    private static IColumn<Participant, SerializableFunction<Participant, ?>> invitationStatusColumn() {
        return new AbstractColumn<Participant, SerializableFunction<Participant, ?>>(Model.of(""),
            with(Participant::getInvitationStatus).andThen(InvitationStatus::getSortOrder)) {
            @Override
            public void populateItem(Item<ICellPopulator<Participant>> item,
                                     String componentId, IModel<Participant> rowModel) {
                IconPanel icon = new IconPanel(componentId);
                Participant participant = rowModel.getObject();
                InvitationStatus invitationStatus = participant.getInvitationStatus();

                icon.setTextAlign(TextAlign.CENTER);
                if (InvitationStatus.ACCEPTED.equals(invitationStatus)) {
                    icon.setType(FontAwesome6IconType.check_s);
                    icon.setColor(IconPanel.Color.SUCCESS);
                } else if (InvitationStatus.TENTATIVE.equals(invitationStatus)) {
                    icon.setType(FontAwesome6IconType.question_s);
                    icon.setColor(IconPanel.Color.INFO);
                } else if (InvitationStatus.DECLINED.equals(invitationStatus)) {
                    icon.setType(FontAwesome6IconType.xmark_s);
                    icon.setColor(IconPanel.Color.DANGER);
                } else if (InvitationStatus.UNINVITED.equals(invitationStatus)) {
                    icon.setType(FontAwesome6IconType.circle_r);
                    icon.setColor(IconPanel.Color.MUTED);
                } else {
                    icon.setType(FontAwesome6IconType.circle_s);
                    icon.setColor(IconPanel.Color.WARNING);
                }

                item.add(icon);
            }

            @Override
            public String getCssClass() {
                return "invitationStatus td-with-btn-xs";
            }
        };
    }

    private static IColumn<Participant, SerializableFunction<Participant, ?>> nameColumn() {
        return new PropertyColumn<Participant, SerializableFunction<Participant, ?>>(new ResourceModel("name", "Name"),
            with(Participant::getSinger).andThen(Person::getSortName),
            "singer.sortName") {
            @Override
            public String getCssClass() {
                return "name";
            }
        };
    }

    private static IColumn<Participant, SerializableFunction<Participant, ?>> voiceColumn() {
        return new EnumColumn<Participant, SerializableFunction<Participant, ?>, Voice>(
            new ResourceModel("voice", "voice"),
            with(Participant::getSinger).andThen(Singer::getVoice),
            "singer.voice") {
            @Override
            public String getCssClass() {
                return "voice";
            }
        };
    }
}
