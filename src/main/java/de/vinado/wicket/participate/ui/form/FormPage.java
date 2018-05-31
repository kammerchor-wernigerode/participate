package de.vinado.wicket.participate.ui.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.AbstractNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import de.vinado.wicket.participate.component.panel.Footer;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.ui.event.event.EventPanel;
import de.vinado.wicket.participate.ui.page.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class FormPage extends BasePage {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private IModel<MemberToEvent> model;

    private boolean signedIn;

    public FormPage() {
        this(new PageParameters());
    }

    public FormPage(final IModel<MemberToEvent> model, final boolean signedIn) {
        this(new PageParameters(), model, signedIn);
    }

    public FormPage(final PageParameters parameters) {
        this(parameters, null, false);
    }

    public FormPage(final PageParameters parameters, IModel<MemberToEvent> model, final boolean signedIn) {
        super(parameters);

        setOutputMarkupId(true);

        this.signedIn = signedIn;

        final String tokenParameter = parameters.get("token").to(String.class);

        if (null != model) {
            this.model = model;
        } else {
            model = new CompoundPropertyModel<>(eventService.getMemberToEvent(tokenParameter));
            this.model = model;
        }
        setDefaultModel(model);

        final Navbar navbar = new Navbar("navbar");
        navbar.setOutputMarkupId(true);
        navbar.setPosition(Navbar.Position.STATIC_TOP);
        navbar.fluid();
        navbar.setBrandName(Model.of(""));
        final IModel<MemberToEvent> finalModel = model;
        navbar.addComponents(new AbstractNavbarComponent(Navbar.ComponentPosition.RIGHT) {
            @Override
            public Component create(final String markupId) {
                return new EventDropDownForm(markupId, finalModel) {
                    @Override
                    protected void onEventChange(final MemberToEvent memberToEvent) {
                        setResponsePage(new FormPage(new CompoundPropertyModel<>(memberToEvent), true));
                    }
                };
            }
        });
        add(navbar);

        final Breadcrumb breadcrumb = new Breadcrumb("breadcrumb");
        breadcrumb.setOutputMarkupPlaceholderTag(true);
        breadcrumb.setVisible(false);
        add(breadcrumb);

        final EventPanel eventPanel = new EventPanel("eventPanel", breadcrumb,
            new CompoundPropertyModel<>(eventService.getEventView(this.model.getObject().getEvent())), false);
        add(eventPanel);

        final FormPanel formPanel = new FormPanel("formPanel", breadcrumb, this.model);
        add(formPanel);

        add(new Footer("footer"));
    }

    @Override
    protected void onConfigure() {
        if (!signedIn) {
            setResponsePage(new FormSignInPage(model));
        }
    }
}
