package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuDivider;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
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
            new NavbarButton(EventsPage.class, new ResourceModel("events", "Events")).setIconType(FontAwesomeIconType.calendar)));
        navbar.addComponents(NavbarComponents.transform(
                Navbar.ComponentPosition.LEFT,
            new NavbarButton(SingersPage.class, new ResourceModel("singers", "Singers")).setIconType(FontAwesomeIconType.group)));
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
                                Singer singer = null;
                                if (null != person) {
                                    singer = personService.hasSinger(person) ? personService.getSinger(person) : null;
                                }

                                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                                modal.setContent(new EditAccountPanel(modal, new CompoundPropertyModel<>(
                                    new EditAccountDTO(user, user.getPerson(), singer))) {
                                    @Override
                                    protected void onConfirm(final User user, final AjaxRequestTarget target) {
                                        ParticipateSession.get().setUser(user);
                                        ParticipateApplication.get().getSecuritySettings().getAuthenticationStrategy().remove();
                                        userLabel = ParticipateSession.get().getUser().getPerson().getDisplayName();
                                        target.add(navbar);
                                        Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
                                    }
                                });
                                modal.show(target);
                            }
                        }.setBody(new ResourceModel("account.edit", "Edit Account")));
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
        super.onConfigure();
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
