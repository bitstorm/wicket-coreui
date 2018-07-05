package it.adelbene;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.registry.RegistryService;
import de.agilecoders.wicket.webjars.WicketWebjars;
import it.adelbene.dubbo.RegisterListener;
import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 * 
 * @see it.adelbene.Start#main(String[])
 */
public class DubboAwsomeMonitorApp extends WebApplication
{

	private final RegisterListener dubboListener = new RegisterListener();

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

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");
		context.start();

		RegistryService registry = (RegistryService)context.getBean("registryService");

		URL subscribeUrl = new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "",
			Constants.INTERFACE_KEY, Constants.ANY_VALUE, Constants.GROUP_KEY, Constants.ANY_VALUE,
			Constants.VERSION_KEY, Constants.ANY_VALUE, Constants.CLASSIFIER_KEY,
			Constants.ANY_VALUE, Constants.CATEGORY_KEY,
			Constants.PROVIDERS_CATEGORY + "," + Constants.CONSUMERS_CATEGORY, Constants.CHECK_KEY,
			String.valueOf(false));

		registry.subscribe(subscribeUrl, dubboListener);
	}

	/**
	 * @return the dubboListener
	 */
	public RegisterListener getDubboListener()
	{
		return dubboListener;
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


}
