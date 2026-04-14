package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.springframework.util.Assert;

public class TextAreaFormControl extends FormControl<String> {

    private static final String CSS_TEMPLATE = """
        #%s {
            height: %spx;
        }\
        """;

    private Integer height;

    public TextAreaFormControl(String id, IModel<String> model) {
        super(id, model);
    }

    @Override
    protected FormComponent<String> createFormComponent(String wicketId) {
        return new TextArea<>(wicketId, getModel()) {

            @Override
            public void renderHead(IHeaderResponse response) {
                if (null == height) {
                    return;
                }

                String markupId = getMarkupId();
                String css = CSS_TEMPLATE.formatted(markupId, height);
                String id = markupId + "-height";
                CssContentHeaderItem item = CssReferenceHeaderItem.forCSS(css, id);
                response.render(item);
            }
        };
    }

    public TextAreaFormControl setHeight(Integer height) {
        Assert.isTrue(null == height || height > 0, "textarea height must be greater than 0");
        this.height = height;
        return this;
    }
}
