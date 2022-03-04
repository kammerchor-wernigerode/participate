package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.components.ShortenedMultilineLabel;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.panels.BnBIconPanel;
import de.vinado.wicket.participate.components.panels.IconPanel;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static de.vinado.wicket.participate.components.Models.map;
import static de.vinado.wicket.participate.components.ShortenedMultilineLabel.Limit;

/**
 * @author Vincent Nadoll
 */
public class ParticipantTable
    extends BootstrapAjaxDataTable<Participant, SerializableFunction<Participant, ?>> {

    public ParticipantTable(String id, ParticipantDataProvider dataProvider) {
        this(id, dataProvider, Collections.emptyList());
    }

    public ParticipantTable(String id, ParticipantDataProvider dataProvider,
                            Collection<IColumn<Participant, SerializableFunction<Participant, ?>>> additionalColumns) {
        super(id, columns(additionalColumns), dataProvider, Integer.MAX_VALUE);
        dataProvider.setSort(with(Participant::getInvitationStatus), SortOrder.ASCENDING);
        hover();
        condensed();
        setOutputMarkupId(true);

        setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new CssClassNameAppender("participants"));
    }

    private static List<IColumn<Participant, SerializableFunction<Participant, ?>>> columns(
        Collection<IColumn<Participant, SerializableFunction<Participant, ?>>> additionalColumns) {
        List<IColumn<Participant, SerializableFunction<Participant, ?>>> columns = new ArrayList<>();
        columns.add(invitationStatusColumn());
        columns.add(nameColumn());
        columns.add(voiceColumn());
        columns.add(attributesColumn());
        columns.add(periodColumn());
        columns.add(commentColumn());
        columns.addAll(additionalColumns);
        return columns;
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
                item.add(new ShortenedMultilineLabel(componentId, map(rowModel, Participant::getComment), new Limit(100)));
            }

            @Override
            public String getCssClass() {
                return "comment m-0";
            }
        };
    }

    private static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }
}
