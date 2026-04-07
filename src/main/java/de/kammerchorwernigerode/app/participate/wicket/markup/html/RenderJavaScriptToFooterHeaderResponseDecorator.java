package de.kammerchorwernigerode.app.participate.wicket.markup.html;

import de.kammerchorwernigerode.app.participate.wicket.WicketApplication;
import org.apache.wicket.Application;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;

public class RenderJavaScriptToFooterHeaderResponseDecorator implements IHeaderResponseDecorator {

    @Override
    public IHeaderResponse decorate(IHeaderResponse response) {
        Application application = Application.get();
        String footerBucketName = application.getMetaData(WicketApplication.footerBucketNameKey);
        return new JavaScriptFilteredIntoFooterHeaderResponse(response, footerBucketName);
    }
}
