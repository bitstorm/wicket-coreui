package it.adelbene.zookeeper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

public class DubboZkManager
{
	private static final String ROOT_NODE = "/dubbo";
	private final CuratorFramework curator;

	public DubboZkManager()
	{
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

		List<String> children = curator.getChildren().usingWatcher(new CuratorWatcher()
		{
			@Override
			public void process(WatchedEvent event) throws Exception
			{
				EventType eventType = event.getType();
				String path = event.getPath();

				switch (eventType)
				{
					case NodeChildrenChanged :
						loadServices(curator.getChildren().forPath(path));
						break;
					default :
						break;
				}

			}
		}).forPath(ROOT_NODE);

		loadServices(children);
	}

	private void loadServices(List<String> children) throws Exception
	{
		Set<String> services = new HashSet<>();

		services.addAll(children);

		for (String service : services)
		{
			listChildrenUsingWatcher(ROOT_NODE + "/" + service + "/consumers");
			listChildrenUsingWatcher(ROOT_NODE + "/" + service + "/providers");
		}
	}

	private void listChildrenUsingWatcher(String node)
	{
		try
		{
			Stat stat = curator.checkExists().forPath(node);
			if (stat != null)
			{
				PathChildrenCache pathcache = new PathChildrenCache(curator, node, true);

				pathcache.getListenable().addListener(new PathChildrenCacheListener()
				{
					@Override
					public void childEvent(CuratorFramework curator, PathChildrenCacheEvent event)
						throws Exception
					{
						List<String> children = curator.getChildren().forPath(node);
						System.out.println(node + ": " + children.size());
					}
				});

				pathcache.start();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
