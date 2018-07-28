package it.adelbene;

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;

import de.agilecoders.wicket.webjars.WicketWebjars;
import it.adelbene.zookeeper.DubboZkManager;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 * 
 * @see it.adelbene.Start#main(String[])
 */
public class DubboAwsomeMonitorApp extends WebApplication
{

	private volatile WebSocketPushBroadcaster broadcaster;
	private volatile DubboZkManager dubboZkManager;

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		WicketWebjars.install(this);
		
		initWebSocket();
		initZkListener();
	}

	private void initZkListener()
	{
		dubboZkManager = new DubboZkManager();
	}

	private void initWebSocket()
	{
		WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(this);
		IWebSocketConnectionRegistry webSocketConnectionRegistry = webSocketSettings.getConnectionRegistry();
		broadcaster = new WebSocketPushBroadcaster (webSocketConnectionRegistry);
	}

	public static DubboAwsomeMonitorApp get()
	{
		Application application = Application.get();

		if (application instanceof DubboAwsomeMonitorApp == false)
		{
			throw new WicketRuntimeException(
				"The application attached to the current thread is not a " +
					DubboAwsomeMonitorApp.class.getSimpleName());
		}

		return (DubboAwsomeMonitorApp)application;
	}

	public DubboZkManager getDubboZkManager()
	{
		return dubboZkManager;
	}
}
