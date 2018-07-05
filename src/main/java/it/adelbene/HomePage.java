package it.adelbene;

import it.adelbene.dubbo.domain.DubboApplication;
import it.adelbene.ui.models.ApplicationsModel;
import it.adelbene.ui.pages.BasePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class HomePage extends BasePage
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public HomePage()
	{
		ApplicationsModel model = new ApplicationsModel();

		add(new ListView<DubboApplication>("rows", model)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -1505114095732809125L;

			@Override
			public void populateItem(final ListItem<DubboApplication> item)
			{
				DubboApplication dubboApplication = item.getModelObject();
				item.add(new Label("name", dubboApplication.getName()));
				item.add(new Label("owner",
					dubboApplication.getOwner() + " " + dubboApplication.getOrganization()));
				item.add(new Label("providerCount", dubboApplication.getProviderCount()));
				item.add(new Label("consumerCount", dubboApplication.getConsumerCount()));
				item.add(new Label("efferentCount", dubboApplication.getEfferentCount()));
				item.add(new Label("afferentCount", dubboApplication.getAfferentCount()));
			}
		});
	}
}
