package de.kammerchorwernigerode.app.participate.wicket.clipboardjs;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class ClipboardJsBehavior extends org.wicketstuff.clipboardjs.ClipboardJsBehavior {

    private final AbstractDefaultAjaxBehavior successBehavior = new AbstractDefaultAjaxBehavior() {

        @Override
        protected void respond(AjaxRequestTarget target) {
            ClipboardJsBehavior.this.onSuccess(target);
        }
    };

    private final AbstractDefaultAjaxBehavior errorBehavior = new AbstractDefaultAjaxBehavior() {

        @Override
        protected void respond(AjaxRequestTarget target) {
            ClipboardJsBehavior.this.onError(target);
        }
    };

    @Override
    public void bind(Component component) {
        super.bind(component);
        component.add(successBehavior);
        component.add(errorBehavior);
    }

    @Override
    public void unbind(Component component) {
        super.unbind(component);
        component.remove(successBehavior);
        component.remove(errorBehavior);
    }

    @Override
    protected void initializeClipboardJs(IHeaderResponse response, Component component) {
        String markupId = component.getMarkupId();
        CharSequence successCallbackScript = successBehavior.getCallbackScript();
        CharSequence errorCallbackScript = errorBehavior.getCallbackScript();

        String javaScript = """
            const clipboard_%s = new ClipboardJS('#%s');
            clipboard_%s.on('success', () => {
              %s
            });
            clipboard_%s.on('error', () => {
              %s
            })\
            """.formatted(markupId, markupId, markupId, successCallbackScript, markupId, errorCallbackScript);

        response.render(OnDomReadyHeaderItem.forScript(javaScript));
    }

    protected void onSuccess(AjaxRequestTarget target) {
    }

    protected void onError(AjaxRequestTarget target) {
    }
}
