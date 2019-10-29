package de.vinado.wicket.participate.ui.administration.tool;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.behavoirs.AjaxDownload;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapFormDecorator;
import de.vinado.wicket.participate.common.generator.EventGenerator;
import de.vinado.wicket.participate.common.generator.SingerGenerator;
import de.vinado.wicket.participate.components.links.BootstrapAjaxButton;
import de.vinado.wicket.participate.components.panels.Collapsible;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.services.DataService;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.services.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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

    public ToolPanel(final String id) {
        super(id);

        final boolean developmentMode = ParticipateApplication.get().isInDevelopmentMode();

        final List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("application-password", "Form Password")) {
            @Override
            public WebMarkupContainer getPanel(final String panelId) {
                return new PasswordPanel(panelId);
            }
        });
        tabs.add(new AbstractTab(new ResourceModel("tools.import-export.persons", "Import/Export Persons")) {
            @Override
            public WebMarkupContainer getPanel(final String panelId) {
                return new ImportExportPersonCSVPanel(panelId);
            }
        });
        if (developmentMode) {
            tabs.add(new AbstractTab(new ResourceModel("tools.generate.data", "Generate Data")) {
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

    private class ImportExportPersonCSVPanel extends Panel {

        private FileUpload file;

        private ImportExportPersonCSVPanel(final String id) {
            super(id);

            final NotificationPanel feedback = new NotificationPanel("feedback");
            add(feedback);

            final Form importForm = new Form("importForm", new CompoundPropertyModel(this));
            add(importForm);

            final WebMarkupContainer importWmc = new WebMarkupContainer("importWmc");
            importWmc.setOutputMarkupId(true);
            importForm.add(importWmc);

            final FileUploadField fileUpload = new FileUploadField("file");
            importWmc.add(fileUpload);

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
            submitBtn.setIconType(FontAwesomeIconType.database);
            submitBtn.setSize(Buttons.Size.Small);
            importWmc.add(submitBtn);

            final Form exportForm = new Form("exportForm", new CompoundPropertyModel(this));
            add(exportForm);

            final WebMarkupContainer exportWmc = new WebMarkupContainer("exportWmc");
            exportForm.setOutputMarkupId(true);
            exportForm.add(exportWmc);

            final AjaxDownload export = new AjaxDownload();

            final BootstrapAjaxButton exportBtn = new BootstrapAjaxButton("exportBtn", new ResourceModel("export", "Export"), Buttons.Type.Primary) {
                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    export.go(target, personService.exportSingers(), "singer-export.csv");
                    target.add(exportForm);
                }

                @Override
                protected FeedbackPanel getFeedbackPanel() {
                    return feedback;
                }
            };
            exportBtn.setIconType(FontAwesomeIconType.save);
            exportBtn.setSize(Buttons.Size.Small);
            exportBtn.add(export);
            exportWmc.add(exportBtn);
        }

        public FileUpload getFile() {
            return file;
        }

        public void setFile(final FileUpload file) {
            this.file = file;
        }
    }

    private class GenerateSampleData extends Panel {

        private Long singerCount = 0L;

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

            final NumberTextField<Long> singerCountTf = new NumberTextField<>("singerCount");
            singerCountTf.add(BootstrapFormDecorator.decorate());
            wmc.add(singerCountTf);

            final BootstrapAjaxButton generateSingersBtn = new BootstrapAjaxButton("generateSingers", Buttons.Type.Primary) {
                @Override
                public void onSubmit(final AjaxRequestTarget target, final Form<?> inner) {
                    final Set<SingerDTO> singerDTOSet = new HashSet<>();

                    SingerGenerator.getInstance().generate((DataService) eventService, singerDTOSet, singerCount);
                    for (SingerDTO singerDTO : singerDTOSet) {
                        final Singer singer = personService.createSinger(singerDTO);
                        eventService.getUpcomingEvents().forEach(event -> eventService.createParticipant(event, singer));
                    }

                    onSuccess(target, form);
                }

                @Override
                protected FeedbackPanel getFeedbackPanel() {
                    return feedback;
                }
            };
            generateSingersBtn.setSize(Buttons.Size.Small);
            generateSingersBtn.setLabel(new ResourceModel("tools.generate.singers", "Generate Singers"));
            generateSingersBtn.setIconType(FontAwesomeIconType.cog);
            wmc.add(generateSingersBtn);

            final NumberTextField<Long> eventCountTf = new NumberTextField<Long>("eventCount");
            eventCountTf.add(BootstrapFormDecorator.decorate());
            wmc.add(eventCountTf);

            final BootstrapAjaxButton generateEventsBtn = new BootstrapAjaxButton("generateEvents", form, Buttons.Type.Primary) {
                @Override
                public void onSubmit(final AjaxRequestTarget target, final Form<?> inner) {
                    final Set<EventDTO> eventDTOSet = new HashSet<>();

                    EventGenerator.getInstance().generate((DataService) eventService, eventDTOSet, eventCount);
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
            generateEventsBtn.setLabel(new ResourceModel("tools.generate.events", "Generate Events"));
            generateEventsBtn.setIconType(FontAwesomeIconType.calendar);
            wmc.add(generateEventsBtn);
        }

        private void onSuccess(final AjaxRequestTarget target, final Form form) {
            target.add(form);
            Snackbar.show(target, new ResourceModel("tools.generate.success", "The generation was successful"));
        }

        public Long getSingerCount() {
            return singerCount;
        }

        public void setSingerCount(final Long singerCount) {
            this.singerCount = singerCount;
        }

        public Long getEventCount() {
            return eventCount;
        }

        public void setEventCount(final Long eventCount) {
            this.eventCount = eventCount;
        }
    }
}
