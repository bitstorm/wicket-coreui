package it.adelbene;

import java.util.Arrays;

import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

import it.adelbene.ui.resources.ChartjsHeaderItemFactory;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage {
	private WicketTester tester;

	@Before
	public void setUp() 
	{
		tester = new WicketTester(new DubboAwsomeMonitorApp());
	}

	@Test
	public void chartJsFactory() 
	{

		JSONObject data = new JSONObject().put("labels", new JSONArray(Arrays.asList("Red", "Green", "Yellow")))
				.put("datasets", new JSONArray().put(new JSONObject().put("data", new JSONArray(Arrays.asList(300, 50, 100)))
						.put("backgroundColor", new JSONArray(Arrays.asList("#FF6384", "#36A2EB", "#FFCE56")))
						.put("hoverBackgroundColor", new JSONArray(Arrays.asList("#FF6384", "#36A2EB", "#FFCE56")))
		));
		OnDomReadyHeaderItem chartJs = ChartjsHeaderItemFactory.buildChartJs("id123", "pie", data, data);

		System.out.println(chartJs.getJavaScript());
	}
}
