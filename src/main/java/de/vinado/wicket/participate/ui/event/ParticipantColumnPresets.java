package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.components.ShortenedMultilineLabel;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.panels.BnBIconPanel;
import de.vinado.wicket.participate.components.panels.IconPanel;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.ui.event.details.CommentColumnHeader;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.vinado.wicket.participate.components.Models.map;

/**
 * @author Vincent Nadoll
 */
public final class ParticipantColumnPresets {

    private ParticipantColumnPresets() {
        throw new UnsupportedOperationException();
    }

    public static List<IColumn<Participant, SerializableFunction<Participant, ?>>> basicReadOnly() {
        List<IColumn<Participant, SerializableFunction<Participant, ?>>> columns = new ArrayList<>();
        columns.add(invitationStatusColumn());
        columns.add(nameColumn());
        columns.add(voiceColumn());
        return columns;
    }

    public static List<IColumn<Participant, SerializableFunction<Participant, ?>>> basicInteractive(
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit,
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onNotify) {
        List<IColumn<Participant, SerializableFunction<Participant, ?>>> columns = basicReadOnly();
        columns.addAll(interactive(onEdit, onNotify));
        return columns;
    }

    public static List<IColumn<Participant, SerializableFunction<Participant, ?>>> detailedReadOnly() {
        List<IColumn<Participant, SerializableFunction<Participant, ?>>> columns = basicReadOnly();
        columns.add(attributesColumn());
        columns.add(periodColumn());
        columns.add(commentColumn());
        return columns;
    }

    public static List<IColumn<Participant, SerializableFunction<Participant, ?>>> detailedInteractive(
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit,
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onNotify) {
        List<IColumn<Participant, SerializableFunction<Participant, ?>>> columns = detailedReadOnly();
        columns.addAll(interactive(onEdit, onNotify));
        return columns;
    }

    private static List<IColumn<Participant, SerializableFunction<Participant, ?>>> interactive(
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit,
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onNotify) {
        return Arrays.asList(
            editColumn(onEdit),
            notifyColumn(onNotify)
        );
    }

    private static IColumn<Participant, SerializableFunction<Participant, ?>> invitationStatusColumn() {
        return new AbstractColumn<Participant, SerializableFunction<Participant, ?>>(Model.of("")) {
            @Override
            public void populateItem(Item<ICellPopulator<Participant>> item,
                                     String componentId, IModel<Participant> rowModel) {
                IconPanel icon = new IconPanel(componentId);
                Participant participant = rowModel.getObject();
                InvitationStatus invitationStatus = participant.getInvitationStatus();

                icon.setTextAlign(TextAlign.CENTER);
                if (InvitationStatus.ACCEPTED.equals(invitationStatus)) {
                    icon.setType(FontAwesomeIconType.check);
                    icon.setColor(IconPanel.Color.SUCCESS);
                } else if (InvitationStatus.DECLINED.equals(invitationStatus)) {
                    icon.setType(FontAwesomeIconType.times);
                    icon.setColor(IconPanel.Color.DANGER);
                } else if (InvitationStatus.UNINVITED.equals(invitationStatus)) {
                    icon.setType(FontAwesomeIconType.circle_thin);
                    icon.setColor(IconPanel.Color.MUTED);
                } else {
                    icon.setType(FontAwesomeIconType.circle);
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

    private static IColumn<Participant, SerializableFunction<Participant, ?>> attributesColumn() {
        return new AbstractColumn<Participant, SerializableFunction<Participant, ?>>(Model.of("")) {
            @Override
            public void populateItem(Item<ICellPopulator<Participant>> cellItem,
                                     String componentId, IModel<Participant> rowModel) {
                cellItem.add(new BnBIconPanel(componentId, rowModel));
            }

            @Override
            public String getCssClass() {
                return "attribute";
            }
        };
    }

    private static IColumn<Participant, SerializableFunction<Participant, ?>> periodColumn() {
        return new AbstractColumn<Participant, SerializableFunction<Participant, ?>>(
            new ResourceModel("period", "Period")) {
            @Override
            public void populateItem(Item<ICellPopulator<Participant>> cellItem,
                                     String componentId, IModel<Participant> rowModel) {
                String formattedDate = "";

                if (null != rowModel.getObject().getFromDate() && null == rowModel.getObject().getToDate()) {
                    formattedDate = "Ab " + new SimpleDateFormat("E HH:mm").format(rowModel.getObject().getFromDate());
                } else if (null != rowModel.getObject().getToDate() && null == rowModel.getObject().getFromDate()) {
                    formattedDate = "Bis " + new SimpleDateFormat("E HH:mm").format(rowModel.getObject().getToDate());
                } else if (null != rowModel.getObject().getFromDate() && null != rowModel.getObject().getToDate()) {
                    formattedDate = new SimpleDateFormat("E HH:mm").format(rowModel.getObject().getFromDate()) + " - "
                        + new SimpleDateFormat("E HH:mm").format(rowModel.getObject().getToDate());
                }

                cellItem.add(new Label(componentId, formattedDate));
            }

            @Override
            public String getCssClass() {
                return "period";
            }
        };
    }

    private static IColumn<Participant, SerializableFunction<Participant, ?>> commentColumn() {
        return new AbstractColumn<Participant, SerializableFunction<Participant, ?>>(
            new ResourceModel("comments", "Comments")) {
            @Override
            public Component getHeader(String componentId) {
                return new CommentColumnHeader(componentId, getDisplayModel()) {
                    @Override
                    protected Component getCommentToggleScope() {
                        return findParent(ParticipantTable.class);
                    }
                };
            }

            @Override
            public void populateItem(Item<ICellPopulator<Participant>> item,
                                     String componentId, IModel<Participant> rowModel) {
                item.add(new ShortenedMultilineLabel(componentId, map(rowModel, Participant::getComment), new ShortenedMultilineLabel.Limit(100)));
            }

            @Override
            public String getCssClass() {
                return "comment m-0";
            }
        };
    }

    private static IColumn<Participant, SerializableFunction<Participant, ?>> editColumn(
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onClick) {
        return new BootstrapAjaxLinkColumn<Participant, SerializableFunction<Participant, ?>>(
            FontAwesomeIconType.pencil, new ResourceModel("invitation.edit", "Edit Invitation")) {
            @Override
            public void onClick(AjaxRequestTarget target, IModel<Participant> rowModel) {
                onClick.accept(target, rowModel);
            }

            @Override
            public String getCssClass() {
                return "edit";
            }
        };
    }

    private static IColumn<Participant, SerializableFunction<Participant, ?>> notifyColumn(
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onClick) {
        return new BootstrapAjaxLinkColumn<Participant, SerializableFunction<Participant, ?>>(
            FontAwesomeIconType.envelope, new ResourceModel("email.send", "Send Email")) {
            @Override
            public void onClick(AjaxRequestTarget target, IModel<Participant> rowModel) {
                onClick.accept(target, rowModel);
            }

            @Override
            public String getCssClass() {
                return "notify";
            }
        };
    }

    private static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }
}
