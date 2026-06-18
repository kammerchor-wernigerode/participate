package de.kammerchorwernigerode.app.participate.person.presentation.components.profile;

import de.kammerchorwernigerode.app.participate.person.presentation.components.form.BasePersonForm;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonDto;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalFooter;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public abstract class ModalPersonForm extends GenericPanel<PersonDto> {

    private final PersonForm form;

    public ModalPersonForm(String id, IModel<PersonDto> model) {
        super(id, model);
        this.form = new PersonForm("form", model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form);

        ModalFooter footer = new ModalFooter("footer")
            .addCloseAction(new ResourceModel("close"))
            .addSubmitAction(new ResourceModel("save"));
        add(footer);
    }

    protected abstract void onSubmit();


    private class PersonForm extends BasePersonForm {

        public PersonForm(String id, IModel<PersonDto> model) {
            super(id, model);
        }

        @Override
        protected void onSubmit() {
            ModalPersonForm.this.onSubmit();
        }
    }
}
