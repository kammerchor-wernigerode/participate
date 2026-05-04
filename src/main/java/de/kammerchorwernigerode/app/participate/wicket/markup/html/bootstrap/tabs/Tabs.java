package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.tabs;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Optional;

public class Tabs<T extends ITab> extends AjaxTabbedPanel<T> {

    public Tabs(String id, List<T> tabs) {
        this(id, tabs, null);
    }

    public Tabs(String id, List<T> tabs, IModel<Integer> model) {
        super(id, tabs, model);
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
            }

            private int getIndex() {
                LoopItem item = findParent(LoopItem.class);
                return item.getIndex();
            }
        };
        link.setOutputMarkupId(true);
        return link;
    }
}
