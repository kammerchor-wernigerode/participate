package de.kammerchorwernigerode.app.participate.wicket;

import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.EventsPage;
import de.kammerchorwernigerode.app.participate.wicket.bootstrap.BootstrapPage;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ParticipatePage extends BootstrapPage {

    public ParticipatePage(PageParameters parameters) {
        super(parameters);
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
        eventsNavbarLink.setIcon(Bi.calendar_fill);
        eventsNavbarLink.setBody(new ResourceModel("events"));
        navbarCollapse.add(eventsNavbarLink);

        visitChildren(BookmarkablePageLink.class, new ActivePageLinkVisitor());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssContentHeaderItem.forCSS("""
            body {
                padding-top: 4.5rem;
            }\
            """, "participate-page"));
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
}
