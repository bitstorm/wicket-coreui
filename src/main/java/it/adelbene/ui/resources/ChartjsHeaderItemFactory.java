/**
 * 
 */
package it.adelbene.ui.resources;

import com.github.openjson.JSONObject;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 * 
 *
 */
public class ChartjsHeaderItemFactory {

  public final static String JS_TEMPLATE =
      "var widget-id-%s = new Chart($('#%s'), { type: '%s', data: %s, options: %s});";

  public static OnDomReadyHeaderItem buildChartJs(String componentId, String widgetType, JSONObject data,
      JSONObject options) {
    String chartJs = String.format(JS_TEMPLATE, componentId, componentId, widgetType, data, options);
    return OnDomReadyHeaderItem.forScript(chartJs);
  }
}
