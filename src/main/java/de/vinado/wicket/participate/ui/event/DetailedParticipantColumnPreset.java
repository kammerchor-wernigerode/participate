package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.components.ShortenedMultilineLabel;
import de.vinado.wicket.participate.components.panels.BnBIconPanel;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.ui.event.details.CommentColumnHeader;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.text.SimpleDateFormat;

public class DetailedParticipantColumnPreset extends BasicParticipantColumnPreset {

    public DetailedParticipantColumnPreset() {
        add(attributesColumn());
        add(periodColumn());
        add(commentColumn());
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
                item.add(new ShortenedMultilineLabel(componentId, rowModel.map(Participant::getComment), new ShortenedMultilineLabel.Limit(100)));
            }

            @Override
            public String getCssClass() {
                return "comment m-0";
            }
        };
    }
}
