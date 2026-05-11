package de.kammerchorwernigerode.app.participate.wicket;

import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.EventsPage;
import de.kammerchorwernigerode.app.participate.person.presentation.ui.overview.PersonsPage;
import de.kammerchorwernigerode.app.participate.security.AuthenticationResolver;
import de.kammerchorwernigerode.app.participate.security.core.AccountUrl;
import de.kammerchorwernigerode.app.participate.wicket.bootstrap.BootstrapPage;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.net.URI;
import jakarta.servlet.ServletContext;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
public class ParticipatePage extends BootstrapPage {

    @SpringBean
    private AuthenticationResolver authenticationResolver;

    @SpringBean
    private AccountUrl accountUrl;

    @SpringBean
    private ServletContext servletContext;

    @Getter
    private Layout layout = Layout.BOXED;

    public ParticipatePage(PageParameters parameters) {
        super(parameters);
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
