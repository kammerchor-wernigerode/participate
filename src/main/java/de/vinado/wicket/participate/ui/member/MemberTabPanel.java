package de.vinado.wicket.participate.ui.member;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.panel.BootstrapPanel;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.dto.MemberDTO;
import de.vinado.wicket.participate.service.PersonService;
import de.vinado.wicket.participate.ui.member.member.AddEditMemberPanel;
import de.vinado.wicket.participate.ui.member.member.MemberListPanel;
import de.vinado.wicket.participate.ui.page.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MemberTabPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    public MemberTabPanel(final String id, final IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);

        final List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("members", "Members")) {
            @Override
            public Panel getPanel(final String panelId) {
                return new BootstrapPanel<List<Member>>(panelId, new CompoundPropertyModel<>(personService.getMemberList()),
                    Model.of("Mitglieder")) {
                    @Override
                    protected Panel newBodyPanel(final String id, final IModel<List<Member>> model) {
                        return new MemberListPanel(id, model);
                    }

                    @Override
                    protected AbstractLink newDefaultBtn(final String id, final IModel<List<Member>> model) {
                        setDefaultBtnLabelModel(new ResourceModel("member.add", "Add Member"));
                        setDefaultBtnIcon(FontAwesomeIconType.plus);
                        return new AjaxLink(id) {
                            @Override
                            public void onClick(final AjaxRequestTarget target) {
                                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                                modal.setContent(new AddEditMemberPanel(modal, new ResourceModel("member.add", "Add Member"),
                                    new CompoundPropertyModel<>(new MemberDTO())));
                                modal.show(target);
                            }
                        };
                    }
                };
            }
        });

        final AjaxBootstrapTabbedPanel<ITab> tabbedPanel = new AjaxBootstrapTabbedPanel<>("tabbedPanel", tabs);
        add(tabbedPanel);
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("members", "Members");
    }
}
