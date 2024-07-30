package de.vinado.wicket.participate.ui.administration.tool;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.Collapsible;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.app.participate.wicket.bt5.button.BootstrapAjaxButton;
import de.vinado.wicket.common.AjaxDownload;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.services.UserService;
import de.vinado.wicket.tabs.LambdaTab;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

import java.util.ArrayList;
import java.util.List;

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

    @SpringBean
    private ApplicationProperties applicationProperties;

    public ToolPanel(String id) {
        super(id);

        add(accordion("collapsible"));
    }

    private WebMarkupContainer accordion(String id) {
        List<ITab> tabs = content();
        return new Collapsible(id, tabs);
    }

    private List<ITab> content() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new LambdaTab(new ResourceModel("application-password", "Form Password"), PasswordPanel::new));
        tabs.add(new LambdaTab(new ResourceModel("tools.import-export.persons", "Import/Export Persons"), ImportExportPersonCSVPanel::new));
        return tabs;
    }


    private class PasswordPanel extends Panel {

        private PasswordPanel(String id) {
            super(id);

            add(new Label("password", applicationProperties.getParticipatePassword()));
        }
    }

    private class ImportExportPersonCSVPanel extends Panel {

        private FileUpload file;

        private ImportExportPersonCSVPanel(String id) {
            super(id);

            NotificationPanel feedback = new NotificationPanel("feedback");
            add(feedback);

            Form importForm = new Form("importForm", new CompoundPropertyModel(this));
            add(importForm);

            WebMarkupContainer importWmc = new WebMarkupContainer("importWmc");
            importWmc.setOutputMarkupId(true);
            importForm.add(importWmc);

            FileUploadField fileUpload = new FileUploadField("file");
            importWmc.add(fileUpload);

            BootstrapAjaxButton submitBtn = new BootstrapAjaxButton("submitBtn", new ResourceModel("import", "Import"),
                Buttons.Type.Primary) {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    if (null != file) {
                        personService.importPersons(file);
                    }
                }

                @Override
                protected FeedbackPanel getFeedbackPanel() {
                    return feedback;
                }
            };
            submitBtn.setIconType(FontAwesome6IconType.database_s);
            submitBtn.setSize(Buttons.Size.Small);
            importWmc.add(submitBtn);

            Form exportForm = new Form("exportForm", new CompoundPropertyModel(this));
            add(exportForm);

            WebMarkupContainer exportWmc = new WebMarkupContainer("exportWmc");
            exportForm.setOutputMarkupId(true);
            exportForm.add(exportWmc);

            AjaxDownload export = new AjaxDownload();

            BootstrapAjaxButton exportBtn = new BootstrapAjaxButton("exportBtn", new ResourceModel("export", "Export"), Buttons.Type.Primary) {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    export.go(target, personService.exportSingers(), "singer-export.csv");
                    target.add(exportForm);
                }

                @Override
                protected FeedbackPanel getFeedbackPanel() {
                    return feedback;
                }
            };
            exportBtn.setIconType(FontAwesome6IconType.floppy_disk_s);
            exportBtn.setSize(Buttons.Size.Small);
            exportBtn.add(export);
            exportWmc.add(exportBtn);
        }

        public FileUpload getFile() {
            return file;
        }

        public void setFile(FileUpload file) {
            this.file = file;
        }
    }
}
