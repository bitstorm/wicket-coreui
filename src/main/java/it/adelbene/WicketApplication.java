package it.adelbene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.RegistryService;

import de.agilecoders.wicket.webjars.WicketWebjars;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see it.adelbene.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	
	private final Set<String> applications = new ConcurrentHashSet<String>();
    private final Map<String, Set<String>> providerServiceApplications = new ConcurrentHashMap<String, Set<String>>();
    private final Map<String, Set<String>> providerApplicationServices = new ConcurrentHashMap<String, Set<String>>();
    private final Map<String, Set<String>> consumerServiceApplications = new ConcurrentHashMap<String, Set<String>>();
    private final Map<String, Set<String>> consumerApplicationServices = new ConcurrentHashMap<String, Set<String>>();
    private final Set<String> services = new ConcurrentHashSet<String>();
    private final Map<String, List<URL>> serviceProviders = new ConcurrentHashMap<String, List<URL>>();
    private final Map<String, List<URL>> serviceConsumers = new ConcurrentHashMap<String, List<URL>>();
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
		super.init();
		WicketWebjars.install(this);

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");
		context.start();

		RegistryService registry = (RegistryService) context.getBean("registryService");

		URL subscribeUrl = new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "", Constants.INTERFACE_KEY,
				Constants.ANY_VALUE, Constants.GROUP_KEY, Constants.ANY_VALUE, Constants.VERSION_KEY,
				Constants.ANY_VALUE, Constants.CLASSIFIER_KEY, Constants.ANY_VALUE, Constants.CATEGORY_KEY,
				Constants.PROVIDERS_CATEGORY + "," + Constants.CONSUMERS_CATEGORY, Constants.CHECK_KEY,
				String.valueOf(false));

		registry.subscribe(subscribeUrl, new NotifyListener() {

			@Override
			public void notify(List<URL> urls) {
				if (urls == null || urls.size() == 0) {
					return;
				}
				Map<String, List<URL>> proivderMap = new HashMap<String, List<URL>>();
				Map<String, List<URL>> consumerMap = new HashMap<String, List<URL>>();
				for (URL url : urls) {
					String application = url.getParameter(Constants.APPLICATION_KEY);
					if (application != null && application.length() > 0) {
						applications.add(application);
					}
					String service = url.getServiceInterface();
					services.add(service);
					String category = url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
					if (Constants.PROVIDERS_CATEGORY.equals(category)) {
						if (Constants.EMPTY_PROTOCOL.equals(url.getProtocol())) {
							serviceProviders.remove(service);
						} else {
							List<URL> list = proivderMap.get(service);
							if (list == null) {
								list = new ArrayList<URL>();
								proivderMap.put(service, list);
							}
							list.add(url);
							if (application != null && application.length() > 0) {
								Set<String> serviceApplications = providerServiceApplications.get(service);
								if (serviceApplications == null) {
									providerServiceApplications.put(service, new ConcurrentHashSet<String>());
									serviceApplications = providerServiceApplications.get(service);
								}
								serviceApplications.add(application);

								Set<String> applicationServices = providerApplicationServices.get(application);
								if (applicationServices == null) {
									providerApplicationServices.put(application, new ConcurrentHashSet<String>());
									applicationServices = providerApplicationServices.get(application);
								}
								applicationServices.add(service);
							}
						}
					} else if (Constants.CONSUMERS_CATEGORY.equals(category)) {
						if (Constants.EMPTY_PROTOCOL.equals(url.getProtocol())) {
							serviceConsumers.remove(service);
						} else {
							List<URL> list = consumerMap.get(service);
							if (list == null) {
								list = new ArrayList<URL>();
								consumerMap.put(service, list);
							}
							list.add(url);
							if (application != null && application.length() > 0) {
								Set<String> serviceApplications = consumerServiceApplications.get(service);
								if (serviceApplications == null) {
									consumerServiceApplications.put(service, new ConcurrentHashSet<String>());
									serviceApplications = consumerServiceApplications.get(service);
								}
								serviceApplications.add(application);

								Set<String> applicationServices = consumerApplicationServices.get(application);
								if (applicationServices == null) {
									consumerApplicationServices.put(application, new ConcurrentHashSet<String>());
									applicationServices = consumerApplicationServices.get(application);
								}
								applicationServices.add(service);
							}

						}
					}
				}
				
				
				if (proivderMap != null && proivderMap.size() > 0) {
					System.out.println("providers:");
					proivderMap.keySet().stream().forEach(System.out::println);
                }
                if (consumerMap != null && consumerMap.size() > 0) {
                	System.out.println("consumers:");
                	consumerMap.keySet().stream().forEach(System.out::println);
                }
			}
		});
	}
}
