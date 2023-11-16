package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.Participant;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;

public class InteractiveColumnPresetDecoratorFactory implements ParticipantColumnListDecoratorFactory {

    private final boolean visible;
    private final SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit;
    private final SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEmail;

    private InteractiveColumnPresetDecoratorFactory(Builder builder) {
        this.visible = builder.visible;
        this.onEdit = builder.onEdit;
        this.onEmail = builder.onEmail;
    }

    @Override
    public ParticipantColumnList decorate(ParticipantColumnList preset) {
        return visible ? new InteractiveColumnPreset(preset, onEdit, onEmail) : preset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private boolean visible = true;
        private SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit;
        private SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEmail;

        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder onEdit(SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEdit) {
            this.onEdit = onEdit;
            return this;
        }

        public Builder onEmail(SerializableBiConsumer<AjaxRequestTarget, IModel<Participant>> onEmail) {
            this.onEmail = onEmail;
            return this;
        }

        public InteractiveColumnPresetDecoratorFactory build() {
            return new InteractiveColumnPresetDecoratorFactory(this);
        }
    }
}
