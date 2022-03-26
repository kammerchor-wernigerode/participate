package de.vinado.wicket.participate.ui.singers;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingersPanel extends BootstrapPanel<SingerFilter> {

    private static final int ROWS_PER_PAGE = 20;

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    public SingersPanel(String id, IModel<SingerFilter> model) {
        super(id, model);
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

        add(filter());
        add(table());
    }

    private void add(AjaxRequestTarget target) {
        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
        modal.setContent(new AddEditSingerPanel(modal, new ResourceModel("singer.add", "Add Singer"),
            new CompoundPropertyModel<>(new SingerDTO())));
        modal.show(target);
    }

    private void email(AjaxRequestTarget target) {
        Email mailData = emailBuilderFactory.create()
            .toPeople(personService.getSingers())
            .build();

        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
        modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
        modal.show(target);
    }

    private SingerFilterForm filter() {
        return new SingerFilterForm("filter", getModel()) {
            @Override
            protected void onApply() {
                send(SingersPanel.this, Broadcast.BREADTH, new SingerFilterIntent(getModelObject()));
            }
        };
    }

    private Component table() {
        SortableDataProvider<Singer, SerializableFunction<Singer, ?>> dataProvider = dataProvider();
        dataProvider.setSort(with(Singer::getSortName), SortOrder.ASCENDING);
        return new BootstrapAjaxDataTable<>("dataTable", columns(), dataProvider, ROWS_PER_PAGE)
            .condensed().hover()
            .setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance())
            .setOutputMarkupId(true)
            .add(new CssClassNameAppender("singers"));
    }

    private SortableDataProvider<Singer, SerializableFunction<Singer, ?>> dataProvider() {
        return new SingerDataProvider(getModel(), personService);
    }

    private List<IColumn<Singer, SerializableFunction<Singer, ?>>> columns() {
        return Arrays.asList(
            nameColumn(),
            emailAddressColumn(),
            voiceColumn(),
            editColumn(),
            emailColumn()
        );
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> nameColumn() {
        return new PropertyColumn<>(new ResourceModel("name", "Name"), with(Singer::getSortName), "sortName") {
            @Override
            public String getCssClass() {
                return "name";
            }
        };
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> emailAddressColumn() {
        return new PropertyColumn<>(new ResourceModel("email", "Email"), with(Singer::getEmail), "email") {
            @Override
            public void populateItem(Item<ICellPopulator<Singer>> item, String componentId, IModel<Singer> rowModel) {
                item.add(new SmartLinkLabel(componentId, getDataModel(rowModel).map(String.class::cast))
                    .add(new CssClassNameAppender("nobusy")));
            }

            @Override
            public String getCssClass() {
                return "emailAddress";
            }
        };
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> voiceColumn() {
        return new EnumColumn<Singer, SerializableFunction<Singer, ?>, Voice>(new ResourceModel("voice", "voice"),
            with(Singer::getVoice), "voice") {
            @Override
            public String getCssClass() {
                return "voice";
            }
        };
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> editColumn() {
        return new BootstrapAjaxLinkColumn<>(FontAwesome5IconType.pencil_alt_s,
            new ResourceModel("singer.edit", "Edit Singer")) {
            @Override
            public void onClick(final AjaxRequestTarget target, final IModel<Singer> rowModel) {
                ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
                final Singer singer = rowModel.getObject();
                modal.setContent(new AddEditSingerPanel(modal, new ResourceModel("singer.edit", "Edit Singer"), new CompoundPropertyModel<>(
                    new SingerDTO(singer))));
                modal.show(target);
            }

            @Override
            public String getCssClass() {
                return "edit";
            }
        };
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> emailColumn() {
        return new BootstrapAjaxLinkColumn<>(FontAwesome5IconType.envelope_s,
            new ResourceModel("email.send", "Send Email")) {
            @Override
            public void onClick(final AjaxRequestTarget target, final IModel<Singer> rowModel) {
                final Person person = rowModel.getObject();

                Email mailData = emailBuilderFactory.create()
                    .to(person)
                    .build();

                ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
                modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                modal.show(target);
            }

            @Override
            public String getCssClass() {
                return "email";
            }
        };
    }

    private static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }

    @Override
    protected IModel<String> titleModel() {
        return new ResourceModel("singers", "Singers");
    }
}
