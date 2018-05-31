package de.vinado.wicket.participate.ui.member.groups;

import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.component.provider.Select2MemberProvider;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.dto.MemberToGroupDTO;
import de.vinado.wicket.participate.event.AjaxUpdateEvent;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2MultiChoice;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AddMemberToGroupPanel extends BootstrapModalPanel<MemberToGroupDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    public AddMemberToGroupPanel(final BootstrapModal modal, final IModel<String> title,
                                 final IModel<MemberToGroupDTO> model) {
        super(modal, title, model);

        final Select2MultiChoice<Member> memberS2mc = new Select2MultiChoice<Member>("members",
                new PropertyModel<>(model, "members"), new Select2MemberProvider(personService));
        memberS2mc.getSettings().setLanguage(getLocale().getLanguage());
        memberS2mc.getSettings().setCloseOnSelect(true);
        memberS2mc.getSettings().setTheme(new Select2BootstrapTheme(true));
        memberS2mc.getSettings().setMinimumInputLength(3);
        memberS2mc.getSettings().setPlaceholder(new ResourceModel("pleaseChoose", "Please choose").getObject());
        memberS2mc.setRequired(true);
        memberS2mc.setOutputMarkupPlaceholderTag(true);
        memberS2mc.setEnabled(true);
        memberS2mc.setLabel(new ResourceModel("selectMember", "Select member"));
        inner.add(memberS2mc);

        addBootstrapFormDecorator(inner);
    }

    @Override
    protected void onSaveSubmit(final IModel<MemberToGroupDTO> model, final AjaxRequestTarget target) {
        personService.assignMemberToGroup(model.getObject());
        Snackbar.show(target, new ResourceModel("addMembersToGroupA", "Members has been "));
        send(getWebPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
    }
}
