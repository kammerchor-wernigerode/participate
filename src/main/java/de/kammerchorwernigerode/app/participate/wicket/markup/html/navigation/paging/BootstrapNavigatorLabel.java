package de.kammerchorwernigerode.app.participate.wicket.markup.html.navigation.paging;

import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigatorLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.navigation.paging.IPageableItems;
import org.apache.wicket.markup.html.panel.Panel;

public class BootstrapNavigatorLabel extends Panel {

    private final IPageableItems pageable;

    public BootstrapNavigatorLabel(String id, IPageableItems pageable) {
        super(id);
        this.pageable = pageable;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer pagination = new WebMarkupContainer("pagination");
        add(pagination);

        NavigatorLabel label = new NavigatorLabel("label", pageable);
        pagination.add(label);
    }
}
