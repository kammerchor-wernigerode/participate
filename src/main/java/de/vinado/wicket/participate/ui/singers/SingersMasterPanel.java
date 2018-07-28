package de.vinado.wicket.participate.ui.singers;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.panel.BootstrapPanel;
import de.vinado.wicket.participate.data.Singer;
import de.vinado.wicket.participate.data.dto.SingerDTO;
import de.vinado.wicket.participate.service.PersonService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingersMasterPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    public SingersMasterPanel(final String id, final IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);

        final BootstrapPanel<List<Singer>> singersPanel = new BootstrapPanel<List<Singer>>("singersPanel",
            new CompoundPropertyModel<>(personService.getSingers()), new ResourceModel("singers", "Singers")) {
            @Override
            protected Panel newBodyPanel(final String id, final IModel<List<Singer>> model) {
                return new SingersPanel(id, model);
            }

            @Override
            protected AbstractLink newDefaultBtn(final String id, final IModel<List<Singer>> model) {
                setDefaultBtnLabelModel(new ResourceModel("singer.add", "Add Singer"));
                setDefaultBtnIcon(FontAwesomeIconType.plus);
                return new AjaxLink(id) {
                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                        modal.setContent(new AddEditSingerPanel(modal, new ResourceModel("singer.add", "Add Singer"),
                            new CompoundPropertyModel<>(new SingerDTO())));
                        modal.show(target);
                    }
                };
            }
        };

        add(singersPanel);
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("singers", "Singers");
    }
}
