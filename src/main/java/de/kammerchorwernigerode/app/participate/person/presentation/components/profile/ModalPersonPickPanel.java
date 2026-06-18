package de.kammerchorwernigerode.app.participate.person.presentation.components.profile;

import de.kammerchorwernigerode.app.participate.person.infrastructure.PersonRecordRepository;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonPickProjection;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonPickProjectionImpl;
import de.kammerchorwernigerode.app.participate.wicket.management.ManagementWicketSession;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.ButtonBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Size;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalFooter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public abstract class ModalPersonPickPanel extends GenericPanel<PersonPickProjection> {

    @SpringBean
    private PersonRecordRepository personRecordRepository;

    private final IModel<List<PersonPickProjection>> persons;

    public ModalPersonPickPanel(String id, IModel<PersonPickProjection> model) {
        super(id, model);
        this.persons = new PersonProjectionModel();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PersonListView personListView = new PersonListView("persons", persons);
        add(personListView);

        ModalFooter footer = new ModalFooter("footer")
            .addCloseAction(new ResourceModel("close"))
            .addAction(id -> new NoopSaveAction(id, new ResourceModel("save")).setVariant(Variant.PRIMARY));
        add(footer);
    }

    protected abstract void onPick();


    private class PersonProjectionModel extends LoadableDetachableModel<List<PersonPickProjection>> {

        @Override
        protected List<PersonPickProjection> load() {
            return personRecordRepository.findAllPersonPickProjectionsByMusicianDeletedIsFalse()
                .map(PersonPickProjectionImpl::new)
                .map(PersonPickProjection.class::cast)
                .toList();
        }
    }

    private class PersonListView extends ListView<PersonPickProjection> {

        public PersonListView(String id, IModel<? extends List<PersonPickProjection>> model) {
            super(id, model);
        }

        @Override
        protected void populateItem(ListItem<PersonPickProjection> item) {
            IModel<PersonPickProjection> model = item.getModel();

            Label nameLabel = new Label("name", model.map(PersonPickProjection::getDisplayName));
            item.add(nameLabel);

            Label emailAddressLabel = new Label("emailAddress", model.map(PersonPickProjection::getEmailAddress));
            emailAddressLabel.setVisible(ManagementWicketSession.get().getRoles()
                .hasAnyRole(new Roles("ORGA,ADMIN")));
            item.add(emailAddressLabel);

            Modal.CloseButton pickButton = new Modal.CloseButton("pickButton") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    ModalPersonPickPanel.this.modelChanging();
                    ModalPersonPickPanel.this.setModelObject(model.getObject());
                    ModalPersonPickPanel.this.modelChanged();

                    onPick();
                }

                @Override
                protected void onConfigure() {
                    super.onConfigure();

                    PersonPickProjection person = model.getObject();
                    setEnabled(!person.hasUser());
                }
            };
            pickButton.add(new ButtonBehavior(Variant.OUTLINE_PRIMARY, Size.SMALL));
            pickButton.setBody(new ResourceModel("pick"));
            item.add(pickButton);
        }
    }


    private static class NoopSaveAction extends ModalFooter.Action {

        public NoopSaveAction(String id, IModel<?> label) {
            super(id, label);
        }

        @Override
        protected AbstractLink createButton(String wicketId) {
            AjaxLink<Void> link = new AjaxLink<>(wicketId) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                }
            };
            link.setEnabled(false);
            return link;
        }
    }
}
