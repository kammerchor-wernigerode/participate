package de.kammerchorwernigerode.app.participate.person.presentation.components.profile;

import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonDto;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonPickProjection;
import de.kammerchorwernigerode.app.participate.person.presentation.model.ProfileDto;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalHeader;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.tabs.Tabs;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileCreationModalContent extends GenericPanel<ProfileDto> {

    public ProfileCreationModalContent(String id, IModel<ProfileDto> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ModalHeader header = new ModalHeader("header")
            .title(new ResourceModel("ProfileCreationModalContent.header.title"));
        header.add(ClassAttributeModifier.append("class", "border-bottom-0"));
        add(header);

        List<ITab> tabs = createTabs();
        Tabs<ITab> tabbedPanel = new Tabs<>("tabs", tabs);
        tabbedPanel.setFill(true);
        add(tabbedPanel);
    }

    private List<ITab> createTabs() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new PersonCreationTab(new ResourceModel("PersonCreationTab.title")));
        tabs.add(new PersonPickTab(new ResourceModel("PersonPickTab.title")));
        return tabs;
    }

    protected void onCreate() {
    }

    protected void onPick() {
    }


    private class PersonCreationTab extends AbstractTab {

        public PersonCreationTab(IModel<String> title) {
            super(title);
        }

        @Override
        public ModalPersonForm getPanel(String panelId) {
            IModel<PersonDto> model = LambdaModel.of(getModel(), ProfileDto::getModel, ProfileDto::setModel);
            return new ModalPersonForm(panelId, model) {

                @Override
                protected void onSubmit() {
                    ProfileCreationModalContent.this.onCreate();
                }
            };
        }
    }

    private class PersonPickTab extends AbstractTab {

        public PersonPickTab(IModel<String> title) {
            super(title);
        }

        @Override
        public ModalPersonPickPanel getPanel(String panelId) {
            IModel<PersonPickProjection> model = LambdaModel.of(getModel(),
                ProfileDto::getInstance, ProfileDto::setInstance);
            return new ModalPersonPickPanel(panelId, model) {

                @Override
                protected void onPick() {
                    ProfileCreationModalContent.this.onPick();
                }
            };
        }
    }
}
