package de.vinado.wicket.participate.ui.singers;

import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.events.SingerUpdateEvent;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Julius Felchow
 */
public class SingersPage extends ParticipatePage {

    private static final long serialVersionUID = -865631168773605240L;

    public SingersPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<SingerFilter> filterModel = CompoundPropertyModel.of(new SingerFilter());
        SingersPanel singersPanel;
        add(singersPanel = new SingersPanel("singersPanel", filterModel));
        singersPanel.add(new UpdateOnEventBehavior<>(SingerUpdateEvent.class));
        singersPanel.add(new UpdateOnEventBehavior<>(SingerFilterIntent.class));
        singersPanel.setOutputMarkupId(true);
    }
}
