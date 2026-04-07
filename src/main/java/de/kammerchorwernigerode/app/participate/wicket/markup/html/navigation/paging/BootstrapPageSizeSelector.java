package de.kammerchorwernigerode.app.participate.wicket.markup.html.navigation.paging;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.RequiredArgsConstructor;

public class BootstrapPageSizeSelector extends GenericPanel<Long> {

    private final long initialPageSize;
    private final DataTable<?, ?> table;

    public BootstrapPageSizeSelector(String id, DataTable<?, ?> table) {
        super(id, new CompoundPropertyModel<>(table.getItemsPerPage()));
        this.initialPageSize = table.getItemsPerPage();
        this.table = table;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        SelectorForm form = new SelectorForm("form", getModel());
        add(form);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        long currentPageSize = table.getItemsPerPage();
        setModelObject(currentPageSize);
    }

    protected void onPageSizeChanged() {
    }


    private class SelectorForm extends Form<Long> {

        private final IModel<List<Long>> pageSizes;

        public SelectorForm(String id, IModel<Long> model) {
            super(id, model);
            this.pageSizes = new PageSizesModel(table.getDataProvider(), initialPageSize);
        }

        @Override
        protected void onSubmit() {
            Long pageSize = getModelObject();

            table.setItemsPerPage(pageSize);
            table.setCurrentPage(0);

            onPageSizeChanged();
            RequestCycle.get().find(AjaxRequestTarget.class)
                .ifPresent(target -> target.add(table));
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            table.setOutputMarkupId(true);

            DropDownChoice<Long> select = new DropDownChoice<>("select", getModel(), pageSizes);
            select.add(new PageSizeSelectBehavior());
            select.setLabel(new ResourceModel("PageSizeSelector.label"));
            add(select);

            SimpleFormComponentLabel label = new SimpleFormComponentLabel("label", select);
            add(label);
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();

            List<Long> options = pageSizes.getObject();
            if (options.contains(getModelObject())) {
                return;
            }

            setModelObject(options.isEmpty() ? initialPageSize : options.getLast());
        }

        @Override
        protected void detachModel() {
            super.detachModel();
            pageSizes.detach();
        }


        private static class PageSizeSelectBehavior extends AjaxFormSubmitBehavior {

            public PageSizeSelectBehavior() {
                super("change");
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.setPreventDefault(true);
            }
        }
    }


    @RequiredArgsConstructor
    private static class PageSizesModel extends LoadableDetachableModel<List<Long>> {

        private static final List<Long> VALUES = List.of(5L, 10L, 25L, 50L, 100L, 250L, 500L);

        private final IDataProvider<?> dataProvider;
        private final long initialPageSize;

        @Override
        protected List<Long> load() {
            long providerSize = dataProvider.size();
            long totalSize = Math.max(1, providerSize);

            SortedSet<Long> options = new TreeSet<>(VALUES);
            options.add(initialPageSize);
            options.add(totalSize);

            return options.stream()
                .filter(option -> option <= totalSize)
                .toList();
        }
    }
}
