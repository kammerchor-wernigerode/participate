package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.model.Participant;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Optional;

public class InteractiveColumnPreset extends ParticipantColumnListDecorator {

    public InteractiveColumnPreset(ParticipantColumnList delegate,
                                   SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit,
                                   SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEmail) {
        super(delegate);

        Optional.ofNullable(onEdit).map(this::editColumn).map(this::add);
        Optional.ofNullable(onEmail).map(this::emailColumn).map(this::add);
    }

    private IColumn<Participant, SerializableFunction<Participant, ?>> editColumn(
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onClick) {
        return new BootstrapAjaxLinkColumn<Participant, SerializableFunction<Participant, ?>>(
            FontAwesome5IconType.pencil_alt_s, new ResourceModel("invitation.edit", "Edit Invitation")) {
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

    private IColumn<Participant, SerializableFunction<Participant, ?>> emailColumn(
        SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onClick) {
        return new BootstrapAjaxLinkColumn<Participant, SerializableFunction<Participant, ?>>(
            FontAwesome5IconType.envelope_s, new ResourceModel("email.send", "Send Email")) {
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
}
