package de.kammerchorwernigerode.app.participate.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class FocusTrackingBehavior extends Behavior {

    @Override
    public void bind(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        String markupId = component.getMarkupId();

        response.render(OnDomReadyHeaderItem.forScript("""
            (function(id) {
              var el = document.getElementById(id);
              if (!el) return;

              if (!el.__restoreFocusBound) {
                el.__restoreFocusBound = true;

                el.addEventListener('focusin', function() {
                  window.wicketRestoreFocusId = id;
                });
              }

              if (!window.wicketRestoreFocusAjaxBound) {
                window.wicketRestoreFocusAjaxBound = true;

                Wicket.Event.subscribe('/ajax/call/complete', function() {
                  var focusedId = window.wicketRestoreFocusId;
                  if (!focusedId) return;

                  window.setTimeout(function() {
                    var focusedEl = document.getElementById(focusedId);
                    if (focusedEl && typeof focusedEl.focus === 'function') {
                      focusedEl.focus();
                    }
                  }, 0);
                });
              }
            })('%s');
            """.formatted(JavaScriptUtils.escapeQuotes(markupId))));
    }
}
