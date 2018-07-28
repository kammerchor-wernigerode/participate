package de.vinado.wicket.participate.ui.singer.singer;

import com.google.common.collect.Lists;
import de.vinado.wicket.participate.component.panel.AbstractTableFilterPanel;
import de.vinado.wicket.participate.data.Singer;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.filter.SingerFilter;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class SingerFilterPanel extends AbstractTableFilterPanel<Singer, SingerFilter> {

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    private TextField searchTermTf;

    public SingerFilterPanel(final String id, final IModel<List<Singer>> model, final IModel<SingerFilter> filterModel) {
        super(id, model, filterModel);

        searchTermTf = new TextField("searchTerm");
        searchTermTf.setLabel(new ResourceModel("search", "Search"));
        inner.add(searchTermTf);

        final DropDownChoice<Voice> voiceDdc = new DropDownChoice<>("voice",
            Lists.newArrayList(Voice.values()), new EnumChoiceRenderer<>());
        voiceDdc.setLabel(new ResourceModel("voice", "Voice"));
        inner.add(voiceDdc);

        final CheckBox showAllCb = new CheckBox("showAll");
        inner.add(showAllCb);

        addBootstrapFormDecorator(form);
    }

    @Override
    protected Component getFocusableFormComponent() {
        return searchTermTf;
    }

    @Override
    public List<Singer> getFilteredData(final SingerFilter filter) {
        return personService.getFilteredSingerList(filter);
    }

    @Override
    public List<Singer> getDefaultData() {
        return personService.getSingers();
    }
}
