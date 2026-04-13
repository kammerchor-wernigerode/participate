package de.kammerchorwernigerode.app.participate.person.presentation.components;

import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntry;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.extensions.markup.html.basic.ILinkParser;
import org.apache.wicket.extensions.markup.html.basic.ILinkRenderStrategy;
import org.apache.wicket.extensions.markup.html.basic.LinkParser;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

public class EmailLinkLabel extends Label implements IGenericComponent<PersonEntry, EmailLinkLabel> {

    private static final String emailPattern = "[\\w\\.\\-\\\\+]+@[\\w\\.\\-]+";

    public EmailLinkLabel(String name, IModel<PersonEntry> model) {
        super(name, model);
    }

    @Override
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        PersonEntry person = getModelObject();
        replaceComponentTagBody(markupStream, openTag, getLink(person));
    }

    private String getLink(PersonEntry person) {
        ILinkParser linkParser = getLinkParser(person);
        return linkParser.parse(person.getEmailAddress());
    }

    private ILinkParser getLinkParser(PersonEntry person) {
        LinkParser linkParser = new LinkParser();
        linkParser.addLinkRenderStrategy(emailPattern, new PersonalEmailLinkRenderStrategy(person));
        return linkParser;
    }


    @RequiredArgsConstructor
    private static class PersonalEmailLinkRenderStrategy implements ILinkRenderStrategy {

        private final PersonEntry person;

        @Override
        public String buildLink(String linkTarget) {
            String name = person.getFirstName() + " " + person.getLastName();

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("name", name);
            uriVariables.put("email", linkTarget);

            String href = UriComponentsBuilder.fromUriString("mailto:{name} <{email}>")
                .encode()
                .buildAndExpand(uriVariables)
                .toUriString();

            return "<a href=\"" + href + "\">" + linkTarget + "</a>";
        }
    }
}
