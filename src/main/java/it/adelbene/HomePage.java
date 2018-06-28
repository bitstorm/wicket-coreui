package it.adelbene;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

import it.adelbene.ui.pages.BasePage;
import it.adelbene.ui.resources.ChartjsHeaderItemFactory;
import it.adelbene.utils.JSONObjectUtil;

public class HomePage extends BasePage {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		JSONObject data = new JSONObject()
			.put("labels", JSONObjectUtil.asJSONArray("Red", "Green", "Yellow"))
			.put("datasets", new JSONArray()
				.put(new JSONObject()
					.put("data", JSONObjectUtil.asJSONArray(300, 50, 100))
					.put("backgroundColor", JSONObjectUtil.asJSONArray("#FF6384", "#36A2EB", "#FFCE56"))
					.put("hoverBackgroundColor", JSONObjectUtil.asJSONArray("#FF6384", "#36A2EB", "#FFCE56"))
		));
		
		JSONObject options = new JSONObject()
			.put("responsive", true);
		
		OnDomReadyHeaderItem headerItem = ChartjsHeaderItemFactory.buildChartJs("#canvas-3", "pie", data, options);
		response.render(headerItem);
	}
}
