package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.tabs;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;

import java.util.List;
import java.util.Optional;

public class Tabs<T extends ITab> extends AjaxTabbedPanel<T> {

    private final String queryParameterKey;

    public Tabs(String id, List<T> tabs) {
        this(id, tabs, null, "tab");
    }

    public Tabs(String id, List<T> tabs, IModel<Integer> model) {
        this(id, tabs, model, "tab");
    }

    public Tabs(String id, List<T> tabs, IModel<Integer> model, String queryParameterKey) {
        super(id, tabs, model);
        this.queryParameterKey = queryParameterKey;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        int selectedTab = resolveSelectedTab();
        setSelectedTab(selectedTab);
    }

    private int resolveSelectedTab() {
        if (Strings.isEmpty(queryParameterKey)) {
            return 0;
        }

        Page page = getPage();
        PageParameters pageParameters = page.getPageParameters();
        StringValue parameter = pageParameters.get(queryParameterKey);
        int selectedTab = parameter.toInt(-1);
        if (selectedTab < 0 || selectedTab >= getTabs().size()) {
            return 0;
        }

        return selectedTab;
    }

    @Override
    protected AjaxLink<Void> newLink(String linkId, int index) {
        AjaxLink<Void> link = new AjaxLink<>(linkId) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                String cssClass = tag.getAttribute("class");

                if (getIndex() == getSelectedTab()) {
                    cssClass += ' ' + getSelectedTabCssClass();
                    tag.put("aria-current", "page");
                }

                tag.put("class", cssClass.trim());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                setSelectedTab(index);
                target.add(Tabs.this);
                onAjaxUpdate(Optional.of(target));
                appendUpdateUrlScript(target, index);
            }

            private int getIndex() {
                LoopItem item = findParent(LoopItem.class);
                return item.getIndex();
            }
        };
        link.setOutputMarkupId(true);
        return link;
    }

    protected void appendUpdateUrlScript(AjaxRequestTarget target, int index) {
        target.appendJavaScript(createUpdateUrlScript(queryParameterKey, index));
    }

    protected static CharSequence createUpdateUrlScript(String queryParameterKey, int index) {
        return """
            var url = new URL(window.location.href);
            url.searchParams.set('%s', %d);
            history.replaceState(null, '', url.toString())\
            """.formatted(queryParameterKey.replace("'", "\\'"), index);
    }
}
