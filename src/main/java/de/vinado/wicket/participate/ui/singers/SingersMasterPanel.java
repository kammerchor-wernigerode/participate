package de.vinado.wicket.participate.ui.singers;

import de.vinado.wicket.participate.behavoirs.UpdateOnEventBehavior;
import de.vinado.wicket.participate.events.SingerUpdateEvent;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.participate.services.PersonService;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingersMasterPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    public SingersMasterPanel(final String id, final IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);

        IModel<SingerFilter> filterModel = CompoundPropertyModel.of(new SingerFilter());
        SingersPanel singersPanel;
        add(singersPanel = new SingersPanel("singersPanel", filterModel));
        singersPanel.add(new UpdateOnEventBehavior<>(SingerUpdateEvent.class));
        singersPanel.add(new UpdateOnEventBehavior<>(SingerFilterIntent.class));
        singersPanel.setOutputMarkupId(true);
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("singers", "Singers");
    }
}
