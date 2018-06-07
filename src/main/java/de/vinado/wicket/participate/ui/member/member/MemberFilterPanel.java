package de.vinado.wicket.participate.ui.member.member;

import de.vinado.wicket.participate.component.panel.AbstractTableFilterPanel;
import de.vinado.wicket.participate.data.Configurable;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.filter.MemberFilter;
import de.vinado.wicket.participate.service.ListOfValueService;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class MemberFilterPanel extends AbstractTableFilterPanel<Member, MemberFilter> {

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    @SuppressWarnings("unused")
    @SpringBean
    private ListOfValueService listOfValueService;

    private TextField searchTermTf;

    public MemberFilterPanel(final String id, final IModel<List<Member>> model, final IModel<MemberFilter> filterModel) {
        super(id, model, filterModel);

        searchTermTf = new TextField("searchTerm");
        searchTermTf.setLabel(new ResourceModel("search", "Search"));
        inner.add(searchTermTf);

        final DropDownChoice<Configurable> voiceDdc = new DropDownChoice<>("voice",
                listOfValueService.getConfigurableList(Voice.class), new ChoiceRenderer<>("name"));
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
    public List<Member> getFilteredData(final MemberFilter filter) {
        return personService.getFilteredMemberList(filter);
    }

    @Override
    public List<Member> getDefaultData() {
        return personService.getMemberList();
    }
}
