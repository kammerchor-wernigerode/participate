package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.ui.event.AccommodationIcon.ViewModel.BedIcon;
import de.vinado.wicket.participate.ui.event.AccommodationIcon.ViewModel.StatusIcon;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import java.io.Serializable;
import java.util.Objects;

import static de.vinado.wicket.participate.model.Accommodation.Status.NO_NEED;

public class AccommodationIcon extends GenericPanel<AccommodationIcon.ViewModel> {

    private static final String ICON_LABEL_PREFIX = "icon.label.event.participant.accommodation";

    public AccommodationIcon(String id, IModel<Accommodation> model) {
        super(id, model.map(AccommodationIcon::translate));
    }

    private static ViewModel translate(Accommodation accommodation) {
        ViewModelFactory factory = new ViewModelFactory();
        return factory.create(accommodation);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(bed("bed"));
        add(icon("icon"));
    }

    protected Component bed(String wicketId) {
        IModel<BedIcon> model = getModel().map(ViewModel::getBedIcon);
        return new TransparentWebMarkupContainer(wicketId)
            .add(new CssClassNameAppender(model.map(BedIcon::getIconCssClassName)))
            .add(new AttributeAppender("data-fa-transform", model.map(BedIcon::getTransformationAttribute)))
            .add(new AttributeAppender("title", getModel().map(ViewModel::getModel).flatMap(TitleModel::new)))
            .setOutputMarkupId(true);
    }

    protected Component icon(String wicketId) {
        IModel<StatusIcon> model = getModel().map(ViewModel::getStatusIcon);
        return new TransparentWebMarkupContainer(wicketId) {

            @Override
            protected void onConfigure() {
                super.onConfigure();

                setVisible(model.map(StatusIcon::isVisible).getObject());
            }
        }
            .add(new CssClassNameAppender(model.map(StatusIcon::getIconCssClassName)))
            .add(new AttributeAppender("data-fa-transform", model.map(StatusIcon::getTransformationAttribute)))
            .setOutputMarkupPlaceholderTag(true);
    }


    private static class ViewModelFactory {

        public ViewModel create(Accommodation model) {
            Accommodation.Status status = Objects.requireNonNullElse(model.getStatus(), NO_NEED);
            BedIcon bedIcon = new BedIcon("fas fa-bed", "shrink-3 down-2 left-4");
            switch (status) {
                case NO_NEED:
                    return new ViewModel(model, bedIcon, StatusIcon.invisible());
                case SEARCHING:
                    return new ViewModel(model, bedIcon, StatusIcon.of("fas fa-search", "shrink-7 up-4 right-7"));
                case OFFERING:
                    return new ViewModel(model, bedIcon, StatusIcon.of("fas fa-hand-holding fa-flip-horizontal", "shrink-5 up-7 right-7"));
                default:
                    throw new IllegalArgumentException();
            }
        }
    }


    @Value
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static class ViewModel implements Serializable {

        Accommodation model;
        BedIcon bedIcon;
        StatusIcon statusIcon;

        @Value
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        static class BedIcon implements Serializable {

            String iconCssClassName;
            String transformationAttribute;
        }

        @Value
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        static class StatusIcon implements Serializable {

            String iconCssClassName;
            String transformationAttribute;
            boolean visible;

            private static StatusIcon of(String iconCssClassName, String transformationAttribute) {
                return new StatusIcon(iconCssClassName, transformationAttribute, true);
            }

            private static StatusIcon invisible() {
                return new StatusIcon("", "", false);
            }
        }
    }

    private class TitleModel extends StringResourceModel {

        public TitleModel(Accommodation model) {
            super(resourceKey(model), AccommodationIcon.this);

            setParameters(model.getBeds());
        }
    }

    private static String resourceKey(Accommodation model) {
        Accommodation.Status status = Objects.requireNonNullElse(model.getStatus(), NO_NEED);
        return ICON_LABEL_PREFIX + "." + status.name();
    }
}
