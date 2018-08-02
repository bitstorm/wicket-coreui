package it.adelbene.zookeeper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import it.adelbene.DubboAwsomeMonitorApp;
import it.adelbene.dubbo.domain.DubboService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.apache.wicket.util.string.Strings;
import org.apache.zookeeper.data.Stat;

public class DubboZkManager
{
	private static final String CONSUMER = "consumer";
	private static final String PROVIDERS = "providers";
	private static final String CONSUMERS = "consumers";
	private static final String ROOT_NODE = "dubbo";

	private final CuratorFramework curator;
	private final Set<String> services;
	private final Map<String, DubboService> appServices;
	private final WebSocketPushBroadcaster broadcaster;
	private final Application application;
	private final CountDownLatch wait4Initialization = new CountDownLatch(1);


	public DubboZkManager(DubboAwsomeMonitorApp application, WebSocketPushBroadcaster broadcaster)
	{
		this.application = application;
		this.broadcaster = broadcaster;
		this.services = new ConcurrentHashSet<>();
		this.appServices = new ConcurrentHashMap<>();

		this.curator = CuratorFrameworkFactory.newClient("localhost:2181", 10000, 10000,
			new RetryNTimes(3, 3000));

		try
		{
			curatorStartup();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void curatorStartup() throws InterruptedException, Exception
	{
		curator.start();
		curator.getZookeeperClient().blockUntilConnectedOrTimedOut();
		String rootPAth = ROOT_NODE;
		Stat stat = curator.checkExists().forPath("/" + rootPAth);

		if (stat != null)
		{
			TreeCache cache = new TreeCache(curator, "/" + rootPAth);
            cache.start();
            
			TreeCacheListener listener = new TreeCacheListener()
			{
				@Override
				public void childEvent(CuratorFramework client, TreeCacheEvent event)
					throws Exception
				{
					switch (event.getType())
					{
						case INITIALIZED :
							wait4Initialization.countDown();
							break;
						case NODE_ADDED :
							processChild(event, true);
							break;
						case NODE_REMOVED :
							processChild(event, false);
							break;
						default :
							break;
					}
				}
			};
            cache.getListenable().addListener(listener);
            wait4Initialization.await();
		}
	}

	private void processChild(TreeCacheEvent event, boolean addOrRemove) throws UnsupportedEncodingException
	{
		String path = event.getData().getPath();
		String lastPathComponent = Strings.lastPathComponent(path, '/');

		if (CONSUMERS.equals(lastPathComponent) ||
			PROVIDERS.equals(lastPathComponent))
		{
			addOrRemoveService(path, addOrRemove);
		}
		else if (services.contains(Strings.beforeLastPathComponent(path, '/')))
		{
			String decodedUrl = URLDecoder.decode(lastPathComponent, "UTF-8");
			
			URL url = URL.valueOf(decodedUrl);
			DubboService service = addOrRemoveAppService(url, addOrRemove);
			broadCastMessage(service);
		}
		
		appServices.forEach((key, value) -> System.out.println(value));
	}

	/**
	 * @param service
	 * @param application2
	 */
	private void broadCastMessage(DubboService service)
	{
		if (service == null || wait4Initialization.getCount() > 0)
		{
			return;
		}
		broadcaster.broadcastAll(application, new NewAppServiceMsg(service));
	}

	private DubboService addOrRemoveAppService(URL url, boolean addOrRemove)
	{
		String side = url.getParameter(Constants.SIDE_KEY);
		String application = url.getParameter(Constants.APPLICATION_KEY);
		String serviceInterface = url.getParameter(Constants.INTERFACE_KEY);

		if (Strings.isEmpty(application) || Strings.isEmpty(side))
		{
			return null;
		}

		DubboService service = null;
		
		boolean isConsumer = CONSUMER.equals(side);

		if (addOrRemove)
		{
			service = appServices.computeIfAbsent(serviceInterface,
				(interfaceName) -> new DubboService(serviceInterface, application));
			
			if (isConsumer)
			{
				service.increaseConsumersCount();
			}
			else
			{
				service.increaseProvidersCount();
			}
		}
		else
		{
			service = appServices.get(serviceInterface);

			if (isConsumer)
			{
				service.decreaseConsumersCount();
			}
			else
			{
				service.decreaseProvidersCount();
			}
		}
		
		return service;
	}

	private void addOrRemoveService(String path, boolean addOrRemove)
	{
		if (addOrRemove)
		{
			services.add(path);
		}
		else
		{
			services.remove(path);
		}
	}

	public Map<String, DubboService> getAppServices()
	{
		return appServices;
	}
}
