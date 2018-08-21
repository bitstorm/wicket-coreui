package it.adelbene.ui.pages;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.settings.JavaScriptLibrarySettings;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public class BasePage extends WebPage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4292083136774208427L;

	public static final CssResourceReference BOOTSTRAP_CSS = new WebjarsCssResourceReference("startbootstrap-sb-admin/current/css/bootstrap.min.css");
	public static final CssResourceReference FONT_AWESOME_CSS = new WebjarsCssResourceReference("startbootstrap-sb-admin/current/font-awesome/css/font-awesome.min.css");
	public static final CssResourceReference COREUI_CSS = new WebjarsCssResourceReference("startbootstrap-sb-admin/current/css/sb-admin.css");
	
	public static final JavaScriptResourceReference BOOTSTRAP_JS = new WebjarsJavaScriptResourceReference("startbootstrap-sb-admin/current/js/bootstrap.min.js");
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(CssHeaderItem.forReference(BOOTSTRAP_CSS)));
		response.render(CssHeaderItem.forReference(FONT_AWESOME_CSS));
		response.render(CssHeaderItem.forReference(COREUI_CSS));
		
		JavaScriptLibrarySettings javaScriptSettings =
				getApplication().getJavaScriptLibrarySettings();
		
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem
				.forReference(javaScriptSettings.getJQueryReference())));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(BOOTSTRAP_JS)));
		
	}
}
