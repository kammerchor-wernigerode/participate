package de.kammerchorwernigerode.app.participate.event.presentation.components.attendee;

import de.kammerchorwernigerode.app.participate.event.model.Accommodation;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Fa;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Fa.Rotation;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Fa.Solid;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.FontAwesomeSolidJsResourceReference;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.Icon;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconType;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Components;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

import lombok.Value;

import static de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.FaBuilder.fa;

public class AccommodationIcon extends GenericPanel<Accommodation> {

    public AccommodationIcon(String id, IModel<Accommodation> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Icon bedIcon = new Icon("bed", fa(Solid.bed).build());
        bedIcon.add(AttributeModifier.replace("data-fa-transform", "shrink-3 down-2 left-4"));
        add(bedIcon);

        IModel<Status> statusModel = getModel().map(this::status);
        Icon statusIcon = new Icon("status", statusModel.map(AccommodationIcon.Status::getIcon)) {

            @Override
            protected void onConfigure() {
                super.onConfigure();

                IModel<IconType> model = getModel();
                setVisible(model.isPresent().getObject());
            }
        };
        statusIcon.add(AttributeModifier.replace("data-fa-transform", statusModel.map(Status::getTransformation)));
        add(statusIcon);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        Components.assertTag(this, tag, "span");

        tag.put("class", "fa-layers fa-fw");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(FontAwesomeSolidJsResourceReference.asHeaderItem());
    }

    private Status status(Accommodation accommodation) {
        StatusFactory statusFactory = new StatusFactory();
        return statusFactory.create(accommodation);
    }


    private static class StatusFactory {

        public Status create(Accommodation accommodation) {
            Accommodation.Status status = accommodation.status();
            return switch (status) {
                case SEARCHING -> {
                    Fa iconType = fa(Solid.magnifying_glass).build();
                    yield new Status(iconType, "shrink-7 up-4 right-7");
                }
                case OFFERING -> {
                    Fa iconType = fa(Solid.hand_holding).rotation(Rotation.flip_horizontal).build();
                    yield new Status(iconType, "shrink-5 up-7 right-7");
                }
                case NO_NEED -> new Status(null, null);
            };
        }
    }

    @Value
    private static class Status implements Serializable {

        IconType icon;
        String transformation;
    }
}
