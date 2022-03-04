package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.model.Participant;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Vincent Nadoll
 */
public class InteractiveParticipantTable extends ParticipantTable {

    private InteractiveParticipantTable(String id,
                                        ParticipantDataProvider dataProvider,
                                        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit,
                                        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onNotify
    ) {
        super(id, dataProvider, toolbar(onEdit, onNotify));
    }

    private static Collection<IColumn<Participant, SerializableFunction<Participant, ?>>> toolbar(
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit,
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onNotify) {
        return Arrays.asList(
            editColumn(onEdit),
            notifyColumn(onNotify)
        );
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

    public static Builder builder(String id, ParticipantDataProvider dataProvider) {
        return new Builder(id, dataProvider);
    }


    @RequiredArgsConstructor
    public static final class Builder {

        private final String id;
        private final ParticipantDataProvider dataProvider;

        private SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit;
        private SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onNotify;

        public Builder onEdit(SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> action) {
            this.onEdit = action;
            return this;
        }

        public Builder onNotify(SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> action) {
            this.onNotify = action;
            return this;
        }

        public InteractiveParticipantTable build() {
            return new InteractiveParticipantTable(id,
                dataProvider,
                Optional.ofNullable(onEdit).orElseGet(() -> (target, participantIModel) -> {
                }),
                Optional.ofNullable(onNotify).orElseGet(() -> (target, participantIModel) -> {
                })
            );
        }
    }
}
