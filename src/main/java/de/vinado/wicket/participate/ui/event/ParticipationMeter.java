package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.EventDetails;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.StyleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticipationMeter extends GenericPanel<EventDetails> {

    public ParticipationMeter(String id, IModel<EventDetails> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(accepted("a"));
        add(declined("d"));
        add(pending("p"));

        add(new GridTemplateColumns(getModel()));
    }

    private Component accepted(String wicketId) {
        return new Section(wicketId, acceptedModel());
    }

    private IModel<Long> acceptedModel() {
        return getModel().map(EventDetails::getAcceptedSum);
    }

    private Component declined(String wicketId) {
        return new Section(wicketId, declinedModel());
    }

    private IModel<Long> declinedModel() {
        return getModel().map(EventDetails::getDeclinedCount);
    }

    private Component pending(String wicketId) {
        return new Section(wicketId, pendingModel());
    }

    private IModel<Long> pendingModel() {
        return getModel().map(EventDetails::getPendingCount);
    }


    private static class Section extends Label implements IGenericComponent<Long, Section> {

        public Section(String id, IModel<Long> model) {
            super(id, model);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(AttributeModifier.append("class", getId()));
        }
    }

    @RequiredArgsConstructor
    private static class GridTemplateColumns extends StyleAttributeModifier {

        private final IModel<EventDetails> model;

        @Override
        protected Map<String, String> update(Map<String, String> oldStyles) {
            Map<String, String> styles = new HashMap<>(oldStyles);
            styles.put("grid-template-columns", value());
            return styles;
        }

        private String value() {
            return fractions()
                .map(append("fr"))
                .collect(Collectors.joining(" "));
        }

        private Stream<Long> fractions() {
            long accepted = get(EventDetails::getAcceptedSum);
            long declined = get(EventDetails::getDeclinedCount);
            long pending = get(EventDetails::getPendingCount);
            return Stream.of(accepted, declined, pending);
        }

        private long get(Function<EventDetails, Long> extractor) {
            return event()
                .map(extractor)
                .orElse(1L);
        }

        private Optional<EventDetails> event() {
            return Optional.ofNullable(model)
                .map(IModel::getObject);
        }

        private static <T> Function<? super T, String> append(String suffix) {
            return self -> self + suffix;
        }
    }
}
