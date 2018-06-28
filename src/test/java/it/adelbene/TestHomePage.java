package it.adelbene;

import java.util.Arrays;
import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import it.adelbene.ui.resources.ChartjsHeaderItemFactory;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage {
  private WicketTester tester;

  @Before
  public void setUp() {
    tester = new WicketTester(new WicketApplication());
  }

  @Test
  public void chartJsFactory() {

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("foo", 1);
    jsonObject.put("bar", "crodino");

    JSONArray jsonArray = new JSONArray(Arrays.asList("a", "ab", "abc", "abcd"));

    OnDomReadyHeaderItem chartJs = ChartjsHeaderItemFactory.buildChartJs("id123", "pie", jsonObject, jsonObject);

    System.out.println(chartJs.getJavaScript());
  }
}
