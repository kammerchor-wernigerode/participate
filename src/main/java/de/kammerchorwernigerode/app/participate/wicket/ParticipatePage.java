package de.kammerchorwernigerode.app.participate.wicket;

import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.EventsPage;
import de.kammerchorwernigerode.app.participate.person.infrastructure.PersonRecordRepository;
import de.kammerchorwernigerode.app.participate.person.presentation.components.profile.ProfileCreationModalContent;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonDto;
import de.kammerchorwernigerode.app.participate.person.presentation.model.ProfileDto;
import de.kammerchorwernigerode.app.participate.person.presentation.ui.overview.PersonsPage;
import de.kammerchorwernigerode.app.participate.security.AuthenticationResolver;
import de.kammerchorwernigerode.app.participate.security.core.AccountUrl;
import de.kammerchorwernigerode.app.participate.wicket.bootstrap.BootstrapPage;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalHiddenEventBehavior;
import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.net.URI;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ParticipatePage extends BootstrapPage {

    private static final String SKIP_PROFILE_CREATION_COOKIE = "participate.management.skip_profile_creation";

    @SpringBean
    private AuthenticationResolver authenticationResolver;

    @SpringBean
    private AccountUrl accountUrl;

    @SpringBean
    private ServletContext servletContext;

    @SpringBean
    private PersonRecordRepository personRecordRepository;

    @Getter
    private Layout layout = Layout.BOXED;

    private final IModel<ProfileDto> profileModel;
    private final IModel<Boolean> hasProfile;

    public ParticipatePage() {
        ProfileDto profileDto = createProfileDto();
        this.profileModel = new CompoundPropertyModel<>(profileDto);
        this.hasProfile = createHasProfileModel(profileDto.getModel());
    }

    public ParticipatePage(PageParameters parameters) {
        super(parameters);
        ProfileDto profileDto = createProfileDto();
        this.profileModel = new CompoundPropertyModel<>(profileDto);
        this.hasProfile = createHasProfileModel(profileDto.getModel());
    }

    private IModel<Boolean> createHasProfileModel(PersonDto personDto) {
        return new LoadableDetachableModel<>() {

            @Override
            protected Boolean load() {
                return personRecordRepository.existsByUserEmailAddress(personDto.getEmailAddress());
            }
        };
    }

    public ParticipatePage setLayout(Layout layout) {
        this.layout = layout;
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BookmarkablePageLink<Void> homeNavbarLink =
            new BookmarkablePageLink<>("homeNavbarLink", Application.get().getHomePage());
        homeNavbarLink.setBody(Model.of("Participate"));
        add(homeNavbarLink);

        WebMarkupContainer navbarCollapse = new WebMarkupContainer("navbarCollapse");
        navbarCollapse.setOutputMarkupId(true);
        add(navbarCollapse);

        WebMarkupContainer navbarToggler = new WebMarkupContainer("navbarToggler");
        navbarToggler.add(AttributeModifier.replace("data-bs-target", "#" + navbarCollapse.getMarkupId()));
        navbarToggler.add(AttributeModifier.replace("aria-controls", navbarCollapse.getMarkupId()));
        navbarToggler.add(AttributeModifier.replace("aria-label", new ResourceModel("navbar-toggler.aria-label")));
        add(navbarToggler);

        BootstrapBookmarkablePageLink<Void> eventsNavbarLink =
            new BootstrapBookmarkablePageLink<>("eventsNavbarLink", EventsPage.class);
        eventsNavbarLink.setVariant(Variant.NAV_LINK);
        eventsNavbarLink.setIcon(Bi.calendar_fill);
        eventsNavbarLink.setBody(new ResourceModel("events"));
        navbarCollapse.add(eventsNavbarLink);

        BootstrapBookmarkablePageLink<Void> personsNavbarLink =
            new BootstrapBookmarkablePageLink<>("personsNavbarLink", PersonsPage.class);
        personsNavbarLink.setVariant(Variant.NAV_LINK);
        personsNavbarLink.setIcon(Bi.people_fill);
        personsNavbarLink.setBody(new ResourceModel("persons"));
        navbarCollapse.add(personsNavbarLink);


        Label userNameLabel = new Label("usernameLabel", new UsernameModel());
        userNameLabel.setRenderBodyOnly(true);
        navbarCollapse.add(userNameLabel);

        AjaxLink<ProfileDto> createProfileLink = new AjaxLink<>("createProfileLink", profileModel) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                BootstrapPage.get().getModal()
                    .content(id -> new ProfileCreationModalContent(id, getModel()))
                    .show(target);
            }
        };
        createProfileLink.setVisible(!hasProfile());
        navbarCollapse.add(createProfileLink);

        Modal profileCreationModal = new Modal("profileCreationModal");
        profileCreationModal.add(new ProfileCreationSkipBehavior());
        add(profileCreationModal);

        if (!hasProfile() && !isProfileCreationDismissed()) {
            profileCreationModal.content(id -> new ProfileCreationModalContent(id, profileModel));
            profileCreationModal.add(new AutoShowModalBehavior());
        }

        ExternalLink accountSettingsLink = new ExternalLink("accountSettingsLink", new AccountUrlModel());
        navbarCollapse.add(accountSettingsLink);

        ExternalLink signOutLink = new ExternalLink("signOutLink", servletContext.getContextPath() + "/logout");
        navbarCollapse.add(signOutLink);

        visitChildren(BookmarkablePageLink.class, new ActivePageLinkVisitor());


        TransparentWebMarkupContainer mainContainer = new TransparentWebMarkupContainer("mainContainer") {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("class", layout.getCssClassName());
            }
        };
        add(mainContainer);
    }

    private boolean hasProfile() {
        return this.hasProfile.getObject();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssContentHeaderItem.forCSS("""
            body {
                padding-top: 4.5rem;
            }\
            """, "participate-page"));
        response.render(ParticipateCssResourceReference.asHeaderItem());
    }

    private ProfileDto createProfileDto() {
        ProfileDto profileDto = new ProfileDto();
        PersonDto personDto = new PersonDto();
        populate(personDto);
        profileDto.setModel(personDto);
        return profileDto;
    }

    private void populate(PersonDto personDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken token) {
            if (token.getPrincipal() instanceof DefaultOidcUser user) {
                personDto.setFirstName(user.getGivenName());
                personDto.setLastName(user.getFamilyName());
                personDto.setFileName(user.getNickName());
                personDto.setEmailAddress(user.getEmail());
            }
        }
    }

    private boolean isProfileCreationDismissed() {
        WebRequest request = (WebRequest) getRequest();
        return null != request.getCookie(SKIP_PROFILE_CREATION_COOKIE);
    }


    private static class ActivePageLinkVisitor implements IVisitor<BookmarkablePageLink<?>, Void> {


        @Override
        public void component(BookmarkablePageLink<?> link, IVisit<Void> visit) {
            if (isActive(link)) {
                link.add(ClassAttributeModifier.append("class", "active"));
                link.add(AttributeModifier.replace("aria-current", "page"));
            }
            visit.dontGoDeeper();
        }

        private boolean isActive(BookmarkablePageLink<?> link) {
            Page page = link.getPage();
            Class<? extends Page> pageClass = page.getClass();
            return pageClass.equals(link.getPageClass());
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum Layout {

        BOXED("container"),
        FLUID("container-fluid"),
        ;

        @Getter
        private final String cssClassName;
    }

    private static class AutoShowModalBehavior extends Behavior {

        @Override
        public void bind(Component component) {
            super.bind(component);

            component.setVisible(true);
        }

        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);

            String id = component.getMarkupId(true);
            response.render(OnDomReadyHeaderItem.forScript(Modal.createActionScript(id, "show")));
        }
    }

    private static class ProfileCreationSkipBehavior extends ModalHiddenEventBehavior {

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            super.onEvent(target);

            WebResponse response = (WebResponse) RequestCycle.get().getResponse();
            Cookie cookie = new Cookie(SKIP_PROFILE_CREATION_COOKIE, "1");
            cookie.setMaxAge(-1);
            response.addCookie(cookie);
        }
    }


    private class UsernameModel extends LoadableDetachableModel<String> {

        @Override
        protected String load() {
            AuthenticatedPrincipal principal = authenticationResolver.resolveUser();
            return principal.getName();
        }
    }

    private class AccountUrlModel extends LoadableDetachableModel<String> {

        @Override
        protected String load() {
            URI uri = accountUrl.get();
            return null == uri ? null : uri.toString();
        }
    }
}
