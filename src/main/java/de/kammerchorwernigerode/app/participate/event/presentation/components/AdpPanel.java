package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class AdpPanel extends GenericPanel<EventEntry> {

    private static final String CSS_TEMPLATE_STRING = """
        #%s {
            grid-template-columns: %dfr %dfr %dfr;
        }\
        """;

    public AdpPanel(String id, IModel<EventEntry> model) {
        super(id, model);
        setOutputMarkupId(true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<EventEntry> model = getModel();

        add(new Section("a", model.map(EventEntry::getAccepted)));
        add(new Section("d", model.map(EventEntry::getDeclined)));
        add(new Section("p", model.map(EventEntry::getPending)));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        String markupId = getMarkupId();
        EventEntry entry = getModelObject();
        Long accepted = entry.getAccepted();
        Long declined = entry.getDeclined();
        Long pending = entry.getPending();

        String css = CSS_TEMPLATE_STRING.formatted(markupId, accepted, declined, pending);
        response.render(CssContentHeaderItem.forCSS(css, markupId + "-fractions"));
    }


    private static class Section extends WebMarkupContainer {

        public Section(String id, IModel<Long> model) {
            super(id, model);
        }

        @Override
        public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
            Object value = getDefaultModelObject();
            if (null != value) {
                replaceComponentTagBody(markupStream, openTag, getDefaultModelObjectAsString(value));
            } else {
                super.onComponentTagBody(markupStream, openTag);
            }
        }
    }
}
