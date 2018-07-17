package it.adelbene.dubbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.util.collections.ConcurrentHashSet;

import it.adelbene.DubboAwsomeMonitorApp;

public class RegisterListener implements NotifyListener
{
	private final Set<String> applications = new ConcurrentHashSet<String>();
	private final Map<String, Set<String>> providerServiceApplications = new ConcurrentHashMap<String, Set<String>>();
	private final Map<String, Set<String>> providerApplicationServices = new ConcurrentHashMap<String, Set<String>>();
	private final Map<String, Set<String>> consumerServiceApplications = new ConcurrentHashMap<String, Set<String>>();
	private final Map<String, Set<String>> consumerApplicationServices = new ConcurrentHashMap<String, Set<String>>();
	private final Set<String> services = new ConcurrentHashSet<String>();
	private final Map<String, List<URL>> serviceProviders = new ConcurrentHashMap<String, List<URL>>();
	private final Map<String, List<URL>> serviceConsumers = new ConcurrentHashMap<String, List<URL>>();
	private final WebSocketPushBroadcaster broadcaster;
	private final DubboAwsomeMonitorApp webApp;

	/**
	 * @param broadcaster
	 * @param webApp 
	 */
	public RegisterListener(WebSocketPushBroadcaster broadcaster, DubboAwsomeMonitorApp webApp)
	{
		this.broadcaster = broadcaster;
		this.webApp = webApp;
	}

	public Set<String> getApplications()
	{
		return Collections.unmodifiableSet(applications);
	}

	public Set<String> getDependencies(String application, boolean reverse)
	{
		if (reverse)
		{
			Set<String> dependencies = new HashSet<String>();
			Set<String> services = providerApplicationServices.get(application);
			if (services != null && services.size() > 0)
			{
				for (String service : services)
				{
					Set<String> applications = consumerServiceApplications.get(service);
					if (applications != null && applications.size() > 0)
					{
						dependencies.addAll(applications);
					}
				}
			}
			return dependencies;
		}
		else
		{
			Set<String> dependencies = new HashSet<String>();
			Set<String> services = consumerApplicationServices.get(application);
			if (services != null && services.size() > 0)
			{
				for (String service : services)
				{
					Set<String> applications = providerServiceApplications.get(service);
					if (applications != null && applications.size() > 0)
					{
						dependencies.addAll(applications);
					}
				}
			}
			return dependencies;
		}
	}

	public Set<String> getServices()
	{
		return Collections.unmodifiableSet(services);
	}

	public Map<String, List<URL>> getServiceProviders()
	{
		return Collections.unmodifiableMap(serviceProviders);
	}

	public List<URL> getProvidersByService(String service)
	{
		List<URL> urls = serviceProviders.get(service);
		return urls == null ? null : Collections.unmodifiableList(urls);
	}

	public List<URL> getProvidersByHost(String host)
	{
		List<URL> urls = new ArrayList<URL>();
		if (host != null && host.length() > 0)
		{
			for (List<URL> providers : serviceProviders.values())
			{
				for (URL url : providers)
				{
					if (host.equals(url.getHost()))
					{
						urls.add(url);
					}
				}
			}
		}
		return urls;
	}

	public List<URL> getProvidersByApplication(String application)
	{
		List<URL> urls = new ArrayList<URL>();
		if (application != null && application.length() > 0)
		{
			for (List<URL> providers : serviceProviders.values())
			{
				for (URL url : providers)
				{
					if (application.equals(url.getParameter(Constants.APPLICATION_KEY)))
					{
						urls.add(url);
					}
				}
			}
		}
		return urls;
	}

	public Set<String> getHosts()
	{
		Set<String> addresses = new HashSet<String>();
		for (List<URL> providers : serviceProviders.values())
		{
			for (URL url : providers)
			{
				addresses.add(url.getHost());
			}
		}
		for (List<URL> providers : serviceConsumers.values())
		{
			for (URL url : providers)
			{
				addresses.add(url.getHost());
			}
		}
		return addresses;
	}

	public Map<String, List<URL>> getServiceConsumers()
	{
		return Collections.unmodifiableMap(serviceConsumers);
	}

	public List<URL> getConsumersByService(String service)
	{
		List<URL> urls = serviceConsumers.get(service);
		return urls == null ? null : Collections.unmodifiableList(urls);
	}

	public List<URL> getConsumersByHost(String host)
	{
		List<URL> urls = new ArrayList<URL>();
		if (host != null && host.length() > 0)
		{
			for (List<URL> consumers : serviceConsumers.values())
			{
				for (URL url : consumers)
				{
					if (host.equals(url.getHost()))
					{
						urls.add(url);
					}
				}
			}
		}
		return Collections.unmodifiableList(urls);
	}

	public List<URL> getConsumersByApplication(String application)
	{
		List<URL> urls = new ArrayList<URL>();
		if (application != null && application.length() > 0)
		{
			for (List<URL> consumers : serviceConsumers.values())
			{
				for (URL url : consumers)
				{
					if (application.equals(url.getParameter(Constants.APPLICATION_KEY)))
					{
						urls.add(url);
					}
				}
			}
		}
		return urls;
	}

	@Override
	public void notify(List<URL> urls)
	{
		if (urls == null || urls.size() == 0)
		{
			return;
		}
		Map<String, List<URL>> proivderMap = new HashMap<String, List<URL>>();
		Map<String, List<URL>> consumerMap = new HashMap<String, List<URL>>();
		
		for (URL url : urls)
		{
			System.out.println(url);
			String application = url.getParameter(Constants.APPLICATION_KEY);
			if (application != null && application.length() > 0)
			{
				applications.add(application);
			}
			String service = url.getServiceInterface();
			services.add(service);
			String category = url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
			if (Constants.PROVIDERS_CATEGORY.equals(category))
			{
				if (Constants.EMPTY_PROTOCOL.equals(url.getProtocol()))
				{
					serviceProviders.remove(service);
				}
				else
				{
					List<URL> list = proivderMap.get(service);
					if (list == null)
					{
						list = new ArrayList<URL>();
						proivderMap.put(service, list);
					}
					list.add(url);
					if (application != null && application.length() > 0)
					{
						Set<String> serviceApplications = providerServiceApplications
							.computeIfAbsent(service, (key) -> new ConcurrentHashSet<String>());

						serviceApplications.add(application);

						Set<String> applicationServices = providerApplicationServices
							.computeIfAbsent(application, (key) -> new ConcurrentHashSet<String>());

						applicationServices.add(service);
					}
				}
			}
			else if (Constants.CONSUMERS_CATEGORY.equals(category))
			{
				if (Constants.EMPTY_PROTOCOL.equals(url.getProtocol()))
				{
					serviceConsumers.remove(service);
				}
				else
				{
					List<URL> list = consumerMap.get(service);
					if (list == null)
					{
						list = new ArrayList<URL>();
						consumerMap.put(service, list);
					}
					list.add(url);
					if (application != null && application.length() > 0)
					{
						Set<String> serviceApplications = consumerServiceApplications
							.computeIfAbsent(service, (key) -> new ConcurrentHashSet<String>());
						;

						serviceApplications.add(application);

						Set<String> applicationServices = consumerApplicationServices
							.computeIfAbsent(application, (key) -> new ConcurrentHashSet<String>());

						applicationServices.add(service);
					}

				}
			}
		}

		if (proivderMap != null && proivderMap.size() > 0)
		{
			serviceProviders.putAll(proivderMap);
		}
		if (consumerMap != null && consumerMap.size() > 0)
		{
			serviceConsumers.putAll(consumerMap);
		}
		
		broadcaster.broadcastAll(webApp, new IWebSocketPushMessage()
		{
		});
	}
}
