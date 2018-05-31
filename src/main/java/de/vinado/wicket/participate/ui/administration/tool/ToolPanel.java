package de.vinado.wicket.participate.ui.administration.tool;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.common.EventGenerator;
import de.vinado.wicket.participate.common.MemberGenerator;
import de.vinado.wicket.participate.component.BootstrapForm;
import de.vinado.wicket.participate.component.Collapsible;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.behavoir.decorator.BootstrapFormDecorator;
import de.vinado.wicket.participate.component.link.BootstrapAjaxButton;
import de.vinado.wicket.participate.data.database.DatabasePopulator;
import de.vinado.wicket.participate.data.dto.EventDTO;
import de.vinado.wicket.participate.data.dto.MemberDTO;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.service.PersonService;
import de.vinado.wicket.participate.service.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ToolPanel extends Panel {

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    @SuppressWarnings("unused")
    @SpringBean
    private UserService userService;

    @Autowired
    private DatabasePopulator databasePopulator;

    public ToolPanel(final String id) {
        super(id);

        final boolean developmentMode = ParticipateApplication.get().isInDevelopmentMode();

        final List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("participatePassword", "Participate password")) {
            @Override
            public WebMarkupContainer getPanel(final String panelId) {
                return new PasswordPanel(panelId);
            }
        });
        tabs.add(new AbstractTab(new ResourceModel("importPersons", "Import persons")) {
            @Override
            public WebMarkupContainer getPanel(final String panelId) {
                return new ImportPersonCSVPanel(panelId);
            }
        });
        if (developmentMode) {
            tabs.add(new AbstractTab(new ResourceModel("syncWithProductionDatabase", "Sync with production database")) {
                @Override
                public WebMarkupContainer getPanel(final String panelId) {
                    return new SyncDatabasePanel(panelId);
                }
            });
            tabs.add(new AbstractTab(new ResourceModel("generateSampleData", "Generate sample data")) {
                @Override
                public WebMarkupContainer getPanel(final String panelId) {
                    return new GenerateSampleData(panelId);
                }
            });
        }

        add(new Collapsible("collapsible", tabs));
    }

    private class PasswordPanel extends Panel {

        private PasswordPanel(final String id) {
            super(id);

            add(new Label("password", ParticipateApplication.get().getApplicationProperties().getParticipatePassword()));
        }
    }

    private class ImportPersonCSVPanel extends Panel {

        private FileUpload file;


        private ImportPersonCSVPanel(final String id) {
            super(id);

            final NotificationPanel feedback = new NotificationPanel("feedback");
            add(feedback);

            final Form form = new Form("form", new CompoundPropertyModel(this));
            add(form);

            final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
            wmc.setOutputMarkupId(true);
            form.add(wmc);

            final FileUploadField fileUpload = new FileUploadField("file");
            fileUpload.add(BootstrapFormDecorator.decorate());
            wmc.add(fileUpload);

            final BootstrapAjaxButton submitBtn = new BootstrapAjaxButton("submitBtn", new ResourceModel("import", "Import"),
                    Buttons.Type.Primary) {
                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    if (null != file) {
                        personService.importPersons(file);
                    }
                }

                @Override
                protected FeedbackPanel getFeedbackPanel() {
                    return feedback;
                }
            };
            submitBtn.setIconType(FontAwesomeIconType.save);
            submitBtn.setSize(Buttons.Size.Small);
            wmc.add(submitBtn);
        }

        public FileUpload getFile() {
            return file;
        }

        public void setFile(final FileUpload file) {
            this.file = file;
        }
    }

    private class GenerateSampleData extends Panel {

        private Long memberCount = 0L;

        private Long eventCount = 0L;


        private GenerateSampleData(final String id) {
            super(id);

            final NotificationPanel feedback = new NotificationPanel("feedback");
            add(feedback);

            final Form form = new Form("form", new CompoundPropertyModel(this));
            add(form);

            final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
            wmc.setOutputMarkupId(true);
            form.add(wmc);

            final NumberTextField<Long> memberCountTf = new NumberTextField<Long>("memberCount");
            memberCountTf.add(BootstrapFormDecorator.decorate());
            wmc.add(memberCountTf);

            final BootstrapAjaxButton generateMembersBtn = new BootstrapAjaxButton("generateMembers", Buttons.Type.Primary) {
                @Override
                public void onSubmit(final AjaxRequestTarget target, final Form<?> inner) {
                    final Set<MemberDTO> memberDTOSet = new HashSet<>();

                    MemberGenerator.getInstance().generate(eventService, memberDTOSet, memberCount);
                    for (MemberDTO memberDTO : memberDTOSet) {
                        personService.createMember(memberDTO);
                    }

                    onSuccess(target, form);
                }

                @Override
                protected FeedbackPanel getFeedbackPanel() {
                    return feedback;
                }
            };
            generateMembersBtn.setSize(Buttons.Size.Small);
            generateMembersBtn.setLabel(new ResourceModel("generateMembers", "Generate members"));
            generateMembersBtn.setIconType(FontAwesomeIconType.cog);
            wmc.add(generateMembersBtn);

            final NumberTextField<Long> eventCountTf = new NumberTextField<Long>("eventCount");
            eventCountTf.add(BootstrapFormDecorator.decorate());
            wmc.add(eventCountTf);

            final BootstrapAjaxButton generateEventsBtn = new BootstrapAjaxButton("generateEvents", form, Buttons.Type.Primary) {
                @Override
                public void onSubmit(final AjaxRequestTarget target, final Form<?> inner) {
                    final Set<EventDTO> eventDTOSet = new HashSet<>();

                    EventGenerator.getInstance().generate(eventService, eventDTOSet, eventCount);
                    for (EventDTO eventDTO : eventDTOSet) {
                        eventService.createEvent(eventDTO);
                    }

                    onSuccess(target, form);
                }

                @Override
                protected FeedbackPanel getFeedbackPanel() {
                    return feedback;
                }
            };
            generateEventsBtn.setSize(Buttons.Size.Small);
            generateEventsBtn.setLabel(new ResourceModel("generateEvents", "Generate events"));
            generateEventsBtn.setIconType(FontAwesomeIconType.calendar);
            wmc.add(generateEventsBtn);
        }

        private void onSuccess(final AjaxRequestTarget target, final Form form) {
            target.add(form);
            Snackbar.show(target, new ResourceModel("generateSampleContentS", "Generierung erfolgreich"));
        }

        public Long getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(final Long memberCount) {
            this.memberCount = memberCount;
        }

        public Long getEventCount() {
            return eventCount;
        }

        public void setEventCount(final Long eventCount) {
            this.eventCount = eventCount;
        }
    }

    private class SyncDatabasePanel extends Panel {

        private String url = "jdbc:mysql://srv.vinado.de:3306/participate";

        private String username = "participate";

        private String passwd = "";

        public SyncDatabasePanel(final String id) {
            super(id);

            final NotificationPanel feedback = new NotificationPanel("feedback");
            feedback.setOutputMarkupPlaceholderTag(true);
            add(feedback);

            final BootstrapForm form = new BootstrapForm("form", new CompoundPropertyModel(this));
            add(form);

            final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
            wmc.setOutputMarkupId(true);
            form.add(wmc);

            final Label label = new Label("label", new ResourceModel("sourceDatabase", "Source database"));
            wmc.add(label);

            final TextField usernameTf = new TextField("username");
            usernameTf.setRequired(true);
            wmc.add(usernameTf);

            final PasswordTextField passwordTf = new PasswordTextField("passwd");
            passwordTf.setLabel(new ResourceModel("password", "Password"));
            passwordTf.setRequired(true);
            wmc.add(passwordTf);

            final TextField dbUrlTf = new TextField("url");
            dbUrlTf.setRequired(true);
            wmc.add(dbUrlTf);

            final BootstrapAjaxButton submitBtn = new BootstrapAjaxButton("submit",
                    new ResourceModel("submit", "Submit"), form, Buttons.Type.Primary) {
                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    databasePopulator.run();

                    // Resets session data to default and reloads the user
                    ParticipateSession.get().clearSessionData();

                    target.add(form);
                }

                @Override
                protected FeedbackPanel getFeedbackPanel() {
                    return feedback;
                }
            };
            submitBtn.setSize(Buttons.Size.Small);
            wmc.add(submitBtn);

            form.addBootstrapFormDecorator();
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(final String passwd) {
            this.passwd = passwd;
        }
    }
}
