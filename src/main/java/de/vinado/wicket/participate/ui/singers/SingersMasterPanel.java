package de.vinado.wicket.participate.ui.singers;

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

        add(new SingersPanel("singersPanel", new CompoundPropertyModel<>(personService.getSingers())));
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("singers", "Singers");
    }
}
