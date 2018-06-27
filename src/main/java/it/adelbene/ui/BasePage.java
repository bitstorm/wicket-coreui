package it.adelbene.ui;

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

	public static final CssResourceReference BOOTSTRAP_CSS = new WebjarsCssResourceReference("bootstrap/current/css/bootstrap.min.css");
	public static final CssResourceReference FONT_AWESOME_CSS = new WebjarsCssResourceReference("font-awesome/current/css/font-awesome.min.css");
	public static final CssResourceReference COREUI_CSS = new WebjarsCssResourceReference("coreui__ajax/current/AJAX_Full_Project_GULP/src/css/style.min.css");
	public static final CssResourceReference SIMPLE_LINE_ICONS_CSS = new WebjarsCssResourceReference("simple-line-icons/current/css/simple-line-icons.css");
	
	public static final JavaScriptResourceReference BOOTSTRAP_JS = new WebjarsJavaScriptResourceReference("bootstrap/current/js/bootstrap.bundle.min.js");
	public static final JavaScriptResourceReference COREUI_JS = new WebjarsJavaScriptResourceReference("coreui__ajax/current/Static_Starter_GULP/src/js/app.js");
	public static final JavaScriptResourceReference CHART_JS = new WebjarsJavaScriptResourceReference("chartjs/current/Chart.min.js");
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(CssHeaderItem.forReference(BOOTSTRAP_CSS)));
		response.render(CssHeaderItem.forReference(SIMPLE_LINE_ICONS_CSS));
		response.render(CssHeaderItem.forReference(FONT_AWESOME_CSS));
		response.render(CssHeaderItem.forReference(COREUI_CSS));
		
		JavaScriptLibrarySettings javaScriptSettings =
				getApplication().getJavaScriptLibrarySettings();
		
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem
				.forReference(javaScriptSettings.getJQueryReference())));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(BOOTSTRAP_JS)));
		response.render(JavaScriptHeaderItem.forReference(CHART_JS));
		response.render(JavaScriptHeaderItem.forReference(COREUI_JS).setDefer(true));
		
	}
}
