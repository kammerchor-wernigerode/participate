package de.vinado.wicket.participate.ui.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuDivider;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.panel.Footer;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.User;
import de.vinado.wicket.participate.data.dto.EditAccountDTO;
import de.vinado.wicket.participate.service.PersonService;
import de.vinado.wicket.participate.service.UserService;
import de.vinado.wicket.participate.ui.account.EditAccountPanel;
import de.vinado.wicket.participate.ui.administration.AdminPage;
import de.vinado.wicket.participate.ui.event.EventPage;
import de.vinado.wicket.participate.ui.member.MemberPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Main page with navigation bar. Authorized starts here.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ParticipatePage extends BasePage {

    public static final String MODAL_ID = "modal";

    @SuppressWarnings("unused")
    @SpringBean
    private UserService userService;

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    private String userLabel;

    private Navbar navbar;

    public ParticipatePage() {
        this(new PageParameters());
    }

    /**
     * @param parameters Page parameters
     */
    public ParticipatePage(final PageParameters parameters) {
        super(parameters);

        navbar = new Navbar("navbar");
        navbar.setOutputMarkupId(true);
        navbar.setPosition(Navbar.Position.STATIC_TOP);
        navbar.fluid();
        navbar.setBrandName(Model.of(ParticipateApplication.get().getApplicationName()));
        navbar.addComponents(NavbarComponents.transform(
                Navbar.ComponentPosition.LEFT,
                new NavbarButton(EventPage.class, new ResourceModel("events", "Events")).setIconType(FontAwesomeIconType.calendar)));
        navbar.addComponents(NavbarComponents.transform(
                Navbar.ComponentPosition.LEFT,
                new NavbarButton(MemberPage.class, new ResourceModel("members", "Members")).setIconType(FontAwesomeIconType.group)));
        navbar.addComponents(NavbarComponents.transform(
                Navbar.ComponentPosition.RIGHT,
                new NavbarDropDownButton(new PropertyModel<>(this, "userLabel"), Model.of(FontAwesomeIconType.user)) {
                    @Override
                    protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
                        final List<AbstractLink> menuButtons = new ArrayList<>();
                        menuButtons.add(new AjaxLink(buttonMarkupId) {
                            @Override
                            public void onClick(final AjaxRequestTarget target) {
                                final User user = ParticipateSession.get().getUser();
                                final Person person = user.getPerson();
                                Member member = null;
                                if (null != person) {
                                    member = personService.hasMember(person) ? personService.getMember(person) : null;
                                }

                                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                                modal.setContent(new EditAccountPanel(modal, new CompoundPropertyModel<>(
                                        new EditAccountDTO(user, user.getPerson(), member))) {
                                    @Override
                                    protected void onConfirm(final User user, final AjaxRequestTarget target) {
                                        ParticipateSession.get().setUser(user);
                                        ParticipateApplication.get().getSecuritySettings().getAuthenticationStrategy().remove();
                                        userLabel = ParticipateSession.get().getUser().getPerson().getDisplayName();
                                        target.add(navbar);
                                        Snackbar.show(target, new ResourceModel("editDataA"));
                                    }
                                });
                                modal.show(target);
                            }
                        }.setBody(new ResourceModel("editAccount", "Edit Account")));
                        if (null != ParticipateSession.get().getUser() && ParticipateSession.get().getRoles().hasRole(Roles.ADMIN)) {
                            menuButtons.add(new BookmarkablePageLink(buttonMarkupId, AdminPage.class)
                                    .setBody(new ResourceModel("administration", "Administration")));
                        }
                        menuButtons.add(new MenuDivider());
                        menuButtons.add(new AjaxLink(buttonMarkupId) {
                            @Override
                            public void onClick(final AjaxRequestTarget target) {
                                ParticipateSession.get().invalidate();
                                setResponsePage(getApplication().getHomePage());
                            }
                        }.setBody(new ResourceModel("logout", "Logout")));
                        return menuButtons;
                    }
                }));
        add(navbar);

        add(new Footer("footer"));
    }

    public String getUserLabel() {
        return userLabel;
    }

    public void setUserLabel(final String userLabel) {
        this.userLabel = userLabel;
    }

    @Override
    protected void onConfigure() {
        if (!ParticipateSession.get().isSignedIn()) {
            ParticipateApplication.get().restartResponseAtSignInPage();
        } else {
            final User user = ParticipateSession.get().getUser();
            if (null != user.getPerson()) {
                setUserLabel(user.getPerson().getDisplayName());
            } else {
                setUserLabel(user.getUsername());
            }
        }
    }
}
