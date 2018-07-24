package it.adelbene.zookeeper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.apache.wicket.util.string.Strings;
import org.apache.zookeeper.data.Stat;

public class DubboZkManager
{
	private static final String PROVIDERS = "providers";
	private static final String CONSUMERS = "consumers";
	private static final String ROOT_NODE = "dubbo";

	private final CuratorFramework curator;
	private final Set<String> services;

	public DubboZkManager()
	{
		services = new ConcurrentHashSet<>();
		curator = CuratorFrameworkFactory.newClient("localhost:2181", 10000, 10000,
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
							break;
						case NODE_ADDED :
							processAddedChild(event);
							break;
						case NODE_REMOVED :
							break;
						default :
							break;
					}
				}
			};
            cache.getListenable().addListener(listener);
		}
	}

	private void processAddedChild(TreeCacheEvent event) throws UnsupportedEncodingException
	{
		String path = event.getData().getPath();
		String lastPathComponent = Strings.lastPathComponent(path, '/');

		if (CONSUMERS.equals(lastPathComponent) ||
			PROVIDERS.equals(lastPathComponent))
		{
			services.add(path);
		}
		else if (services.contains(Strings.beforeLastPathComponent(path, '/')))
		{
			String decodedUrl = URLDecoder.decode(lastPathComponent, "UTF-8");
			System.out.println(decodedUrl);
			
			URL url = URL.valueOf(decodedUrl);
			String side = url.getParameter(Constants.SIDE_KEY);
			String application = url.getParameter(Constants.APPLICATION_KEY);
			String serviceInterface = url.getParameter(Constants.INTERFACE_KEY);

			System.out.println(String.join(", ", side, application, serviceInterface));
		}
	}
}
