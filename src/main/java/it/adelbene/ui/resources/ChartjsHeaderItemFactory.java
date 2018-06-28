/**
 * 
 */
package it.adelbene.ui.resources;

import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import com.github.openjson.JSONObject;

/**
 * 
 *
 */
public class ChartjsHeaderItemFactory {

  public final static String JS_TEMPLATE =
      "new Chart($('%s'), { type: '%s', data: %s, options: %s});";

  public static OnDomReadyHeaderItem buildChartJs(String jqSelector, String widgetType, JSONObject data,
      JSONObject options) {
    String chartJs = String.format(JS_TEMPLATE, jqSelector, widgetType, data, options);
    return OnDomReadyHeaderItem.forScript(chartJs);
  }
}
