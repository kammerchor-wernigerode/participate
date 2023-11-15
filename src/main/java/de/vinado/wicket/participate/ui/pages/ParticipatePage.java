package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuDivider;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.app.participate.management.wicket.ManagementSession;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.participate.components.panels.EditAccountPanel;
import de.vinado.wicket.participate.components.panels.Footer;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.EditAccountDTO;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.services.UserService;
import de.vinado.wicket.participate.ui.administration.AdminPage;
import de.vinado.wicket.participate.ui.event.EventsPage;
import de.vinado.wicket.participate.ui.singers.SingersPage;
import de.vinado.wicket.participate.wicket.inject.ApplicationName;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ParticipatePage extends BasePage {

    public static final String MODAL_ID = "modal";

    @SpringBean
    private ApplicationName applicationName;

    @SuppressWarnings("unused")
    @SpringBean
    private UserService userService;

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        assertSignedIn();

        add(navbar("navbar"));

        add(new Footer("footer"));
    }

    private Component navbar(String wicketId) {
        Navbar navbar = new Navbar(wicketId);
        navbar.setOutputMarkupId(true);
        navbar.setPosition(Navbar.Position.TOP);
        navbar.setCollapseBreakdown(Navbar.CollapseBreakpoint.Medium);
        navbar.setBrandName(applicationName::get);
        navbar.addComponents(NavbarComponents.transform(
            Navbar.ComponentPosition.LEFT,
            new NavbarButton(EventsPage.class, new ResourceModel("events", "Events")).setIconType(FontAwesome5IconType.calendar_s),
            new NavbarButton(SingersPage.class, new ResourceModel("singers", "Singers")).setIconType(FontAwesome5IconType.users_s)));
        navbar.addComponents(NavbarComponents.transform(
            Navbar.ComponentPosition.RIGHT,
            new NavbarDropDownButton(new UsernameModel(), Model.of(FontAwesome5IconType.user_s)) {
                @Override
                protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
                    final List<AbstractLink> menuButtons = new ArrayList<>();
                    menuButtons.add(new AjaxLink<Void>(buttonMarkupId) {
                        @Override
                        public void onClick(final AjaxRequestTarget target) {
                            final User user = getSession().getMetaData(ManagementSession.user);
                            final Person person = user.getPerson();
                            Singer singer = null;
                            if (null != person) {
                                singer = personService.hasSinger(person) ? personService.getSinger(person) : null;
                            }

                            ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
                            modal.setContent(new EditAccountPanel(modal, new CompoundPropertyModel<>(
                                new EditAccountDTO(user, user.getPerson(), singer))) {
                                @Override
                                protected void onConfirm(final User user, final AjaxRequestTarget target) {
                                    getSession().setMetaData(ManagementSession.user, user);
                                    Application.get().getSecuritySettings().getAuthenticationStrategy().remove();
                                    target.add(navbar);
                                    Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
                                }
                            });
                            modal.show(target);
                        }
                    }.setBody(new ResourceModel("account.edit", "Edit Account")));
                    if (null != getSession().getMetaData(ManagementSession.user) && AbstractAuthenticatedWebSession.get().getRoles().hasRole(Roles.ADMIN)) {
                        menuButtons.add(new BookmarkablePageLink(buttonMarkupId, AdminPage.class)
                            .setBody(new ResourceModel("administration", "Administration")));
                    }
                    menuButtons.add(new MenuDivider());
                    menuButtons.add(new ExternalLink(buttonMarkupId, "/logout")
                        .setBody(new ResourceModel("logout", "Logout")));
                    return menuButtons;
                }
            }));
        return navbar;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        assertSignedIn();
    }

    private static void assertSignedIn() {
        if (!AbstractAuthenticatedWebSession.get().isSignedIn()) {
            ((AuthenticatedWebApplication) AuthenticatedWebApplication.get()).restartResponseAtSignInPage();
        }
    }


    private static class UsernameModel implements IModel<String> {

        @Override
        public String getObject() {
            Session session = Session.get();
            return Optional.ofNullable(session.getMetaData(ManagementSession.user))
                .map(User::getPerson)
                .map(Person::getDisplayName)
                .or(username(session))
                .orElse(null);
        }

        private static Supplier<Optional<String>> username(Session session) {
            return () -> Optional.ofNullable(session.getMetaData(ManagementSession.user)).map(User::getUsername);
        }
    }
}
