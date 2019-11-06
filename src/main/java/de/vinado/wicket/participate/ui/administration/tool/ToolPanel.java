package de.vinado.wicket.participate.ui.administration.tool;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.behavoirs.AjaxDownload;
import de.vinado.wicket.participate.components.links.BootstrapAjaxButton;
import de.vinado.wicket.participate.components.panels.Collapsible;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.services.UserService;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                        try {
                            List<Event> events = eventService.listEvents();
                            personService.importPersons(file.getInputStream())
                                .forEach(singer -> events.parallelStream()
                                    .forEach(event -> eventService.create(event, singer))
                                );
                        } catch (IOException e) {
                            throw new WicketRuntimeException("Could not read from uploaded file", e);
                        }
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
}
