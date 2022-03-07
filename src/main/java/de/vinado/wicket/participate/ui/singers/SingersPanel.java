package de.vinado.wicket.participate.ui.singers;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.events.SingerUpdateEvent;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingersPanel extends BootstrapPanel<List<Singer>> {

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    private SimpleDataProvider<Singer, String> dataProvider;
    private BootstrapAjaxDataTable<Singer, String> dataTable;

    public SingersPanel(final String id, final IModel<List<Singer>> model) {
        super(id, model);

        final SingerFilterPanel filterPanel = new SingerFilterPanel("filterPanel", model, new CompoundPropertyModel<>(new SingerFilter())) {
            @Override
            public SimpleDataProvider<Singer, ?> getDataProvider() {
                return dataProvider;
            }

            @Override
            public DataTable<Singer, ?> getDataTable() {
                return dataTable;
            }
        };
        add(filterPanel);

        dataProvider = new SimpleDataProvider<Singer, String>(model.getObject()) {
            @Override
            public String getDefaultSort() {
                return "sortName";
            }
        };

        final List<IColumn<Singer, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(new ResourceModel("name", "Name"), "sortName", "sortName"));
        columns.add(new PropertyColumn<>(new ResourceModel("email", "Email"), "email", "email"));
        columns.add(new EnumColumn<Singer, String, Voice>(new ResourceModel("voice", "voice"), "voice", "voice"));
        columns.add(new BootstrapAjaxLinkColumn<Singer, String>(FontAwesome5IconType.pencil_alt_s, new ResourceModel("singer.edit", "Edit Singer")) {
            @Override
            public void onClick(final AjaxRequestTarget target, final IModel<Singer> rowModel) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                final Singer singer = rowModel.getObject();
                modal.setContent(new AddEditSingerPanel(modal, new ResourceModel("singer.edit", "Edit Singer"), new CompoundPropertyModel<>(
                    new SingerDTO(singer))));
                modal.show(target);
            }
        });
        columns.add(new BootstrapAjaxLinkColumn<Singer, String>(FontAwesome5IconType.envelope_s, new ResourceModel("email.send", "Send Email")) {
            @Override
            public void onClick(final AjaxRequestTarget target, final IModel<Singer> rowModel) {
                final Person person = rowModel.getObject();

                Email mailData = emailBuilderFactory.create()
                    .to(person)
                    .build();

                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                modal.show(target);
            }
        });

        dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 20);
        dataTable.setOutputMarkupId(true);
        dataTable.hover();
        dataTable.condensed();
        add(dataTable);
    }

    @Override
    protected IModel<String> titleModel() {
        return new ResourceModel("singers", "Singers");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addQuickAccessAction(AjaxAction.create(new ResourceModel("singer.add", "Add Singer"),
            FontAwesome5IconType.plus_s,
            this::add));
        addDropdownAction(AjaxAction.create(new ResourceModel("email.send", "Send Email"),
            FontAwesome5IconType.envelope_s,
            this::email));
    }

    private void add(AjaxRequestTarget target) {
        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new AddEditSingerPanel(modal, new ResourceModel("singer.add", "Add Singer"),
            new CompoundPropertyModel<>(new SingerDTO())));
        modal.show(target);
    }

    private void email(AjaxRequestTarget target) {
        Email mailData = emailBuilderFactory.create()
            .toPeople(getModelObject())
            .build();

        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
        modal.show(target);
    }

    @Override
    public void onEvent(final IEvent<?> event) {
        super.onEvent(event);
        final Object payload = event.getPayload();
        if (payload instanceof SingerUpdateEvent) {
            final SingerUpdateEvent updateEvent = (SingerUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            setModelObject(personService.getSingers());
            dataProvider.set(getModelObject());
            target.add(dataTable);
        }
    }
}
