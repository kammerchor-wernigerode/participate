package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

public class ComponentListView extends ListView<Component> {

    public ComponentListView(String id, List<Component> list) {
        super(id, list);
    }

    @Override
    protected void populateItem(ListItem<Component> item) {
        Component component = item.getModelObject();
        item.add(component);
    }
}
