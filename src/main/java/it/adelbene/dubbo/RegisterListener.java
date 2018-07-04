package it.adelbene.dubbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.registry.NotifyListener;

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
							.computeIfAbsent(service, (key) -> new ConcurrentHashSet<String>());;

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
	}
}