package it.adelbene;

import it.adelbene.dubbo.domain.DubboService;
import it.adelbene.ui.models.ApplicationsModel;
import it.adelbene.ui.pages.BasePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class HomePage extends BasePage
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public HomePage()
	{
		ApplicationsModel model = new ApplicationsModel();
		
		WebMarkupContainer markupContainer = new WebMarkupContainer("listContainer");
		markupContainer.setOutputMarkupId(true);
		
		add(new WebSocketBehavior()
		{
			
			/* (non-Javadoc)
			 * @see org.apache.wicket.protocol.ws.api.WebSocketBehavior#onPush(org.apache.wicket.protocol.ws.api.WebSocketRequestHandler, org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage)
			 */
			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message)
			{
				super.onPush(handler, message);
				handler.add(markupContainer);
			}
		});
		
		markupContainer.add(new ListView<DubboService>("rows", model)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -1505114095732809125L;

			@Override
			public void populateItem(final ListItem<DubboService> item)
			{
				DubboService dubboservice = item.getModelObject();
				item.add(new Label("name", dubboservice.getName()));
				item.add(new Label("application", dubboservice.getApplication()));
				item.add(new Label("providerCount", dubboservice.getProviderCount()));
				item.add(new Label("consumerCount", dubboservice.getConsumerCount()));
			}
		});
		
		add(markupContainer);
	}
}
