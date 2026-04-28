package de.kammerchorwernigerode.app.participate.person.presentation.ui.overview;

import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import de.kammerchorwernigerode.app.participate.person.presentation.components.EmailLinkLabel;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntry;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntryRepository;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntrySpecification;
import de.kammerchorwernigerode.app.participate.wicket.bootstrap.BootstrapPage;
import de.kammerchorwernigerode.app.participate.wicket.clipboardjs.ClipboardJsBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.ContentDivision;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.Icon;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.EnumLambdaColumn;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.filter.BootstrapMultipleChoiceFilter;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.filter.BootstrapTextFilter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.kammerchorwernigerode.app.participate.person.presentation.ui.overview.PersonDataProvider.NAME_SORT_PROPERTY;

public class PersonTablePanel extends GenericPanel<PersonEntrySpecification> {

    @SpringBean
    private PersonEntryRepository personEntryRepository;

    public PersonTablePanel(String id, IModel<PersonEntrySpecification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<PersonEntrySpecification> model = getModel();
        PersonDataProvider dataProvider = new PersonDataProvider(personEntryRepository, model);
        dataProvider.setSort(NAME_SORT_PROPERTY, SortOrder.ASCENDING);


        FilterForm<PersonEntrySpecification> filterForm = new FilterForm<>("filterForm", dataProvider);
        add(filterForm);

        List<IColumn<PersonEntry, String[]>> columns = createColumns();
        int rowsPerPage = getRowsPerPage();
        PersonTable table = new PersonTable("table", columns, dataProvider, rowsPerPage);
        table.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        table.addTopToolbar(new FilterToolbar(table, filterForm));
        filterForm.add(table);
    }

    private List<IColumn<PersonEntry, String[]>> createColumns() {
        List<IColumn<PersonEntry, String[]>> columns = new ArrayList<>();
        columns.add(new PersonNameColumn<>(new ResourceModel("person.name"), NAME_SORT_PROPERTY));
        columns.add(new VoiceColumn<>(new ResourceModel("musician.voice"), new String[]{"voiceOrder"}));
        columns.add(new EmailAddressColumn<>(new ResourceModel("person.emailAddress")));
        return columns;
    }

    private int getRowsPerPage() {
        Session session = Session.get();
        Long rowsPerPage = session.getMetaData(PersonTable.personTablePageSize);
        return Math.toIntExact(rowsPerPage);
    }


    private static class PersonNameColumn<S> extends LambdaColumn<PersonEntry, S>
        implements IFilteredColumn<PersonEntry, S> {

        public PersonNameColumn(IModel<String> displayModel, S sortProperty) {
            super(displayModel, sortProperty, PersonNameColumn::printName);
        }

        @Override
        public Component getFilter(String componentId, FilterForm<?> form) {
            IModel<PersonEntrySpecification> specModel = form.getModel()
                .filter(PersonEntrySpecification.class::isInstance)
                .map(PersonEntrySpecification.class::cast);
            IModel<String> model = LambdaModel.of(specModel, PersonEntrySpecification::getName,
                PersonEntrySpecification::setName);
            return new BootstrapTextFilter<>(componentId, model, form);
        }

        private static String printName(PersonEntry entry) {
            String fileName = entry.getFileName();
            if (!Strings.isEmpty(fileName)) {
                return fileName;
            }

            String lastName = entry.getLastName();
            String firstName = entry.getFirstName();
            return firstName + " " + lastName;
        }
    }

    private static class VoiceColumn<S> extends EnumLambdaColumn<PersonEntry, Voice, S>
        implements IFilteredColumn<PersonEntry, S> {

        public VoiceColumn(IModel<String> displayModel, S sortProperty) {
            super(displayModel, sortProperty, PersonEntry::getVoice);
        }

        @Override
        public Component getFilter(String componentId, FilterForm<?> form) {
            IModel<PersonEntrySpecification> specModel = form.getModel()
                .filter(PersonEntrySpecification.class::isInstance)
                .map(PersonEntrySpecification.class::cast);
            IModel<List<Voice>> model = specModel.map(PersonEntrySpecification::getVoices);
            List<Voice> choices = Arrays.asList(Voice.values());
            IChoiceRenderer<Voice> renderer = new EnumChoiceRenderer<>(form);
            return new BootstrapMultipleChoiceFilter<>(componentId, model, form, choices, renderer, true);
        }
    }

    private static class EmailAddressColumn<S> extends AbstractColumn<PersonEntry, S>
        implements IFilteredColumn<PersonEntry, S> {

        public EmailAddressColumn(IModel<String> displayModel) {
            super(displayModel);
        }

        @Override
        public void populateItem(Item<ICellPopulator<PersonEntry>> cellItem, String componentId,
                                 IModel<PersonEntry> rowModel) {
            ContentDivision div = new ContentDivision(componentId);
            div.add(ClassAttributeModifier.append("class", "d-flex gap-2"));

            RepeatingView view = new RepeatingView(div.getChildId());
            div.add(view);


            EmailLinkLabel emailLinkLabel = new EmailLinkLabel(view.newChildId(), rowModel);
            view.add(emailLinkLabel);

            Icon.Panel icon = new Icon.Panel(view.newChildId(), Bi.copy);
            icon.add(new ClipboardJsBehavior() {

                @Override
                protected void onSuccess(AjaxRequestTarget target) {
                    String message = icon.getString("EmailAddressColumn.copy.onSuccess");
                    icon.success(message);
                    BootstrapPage.get().updateToaster();
                }
            }.setTarget(emailLinkLabel));
            icon.add(AttributeModifier.replace("role", "button"));
            view.add(icon);

            cellItem.add(div);
        }

        @Override
        public Component getFilter(String componentId, FilterForm<?> form) {
            IModel<PersonEntrySpecification> specModel = form.getModel()
                .filter(PersonEntrySpecification.class::isInstance)
                .map(PersonEntrySpecification.class::cast);
            IModel<String> model = LambdaModel.of(specModel, PersonEntrySpecification::getEmailAddress,
                PersonEntrySpecification::setEmailAddress);
            return new BootstrapTextFilter<>(componentId, model, form);
        }
    }
}
