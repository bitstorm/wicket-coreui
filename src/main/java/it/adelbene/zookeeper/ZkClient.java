package it.adelbene.zookeeper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

public class ZkClient {

	private static final String ROOT_NODE = "/dubbo";
	private static CuratorFramework curator;

	public static void main(String[] args) throws Exception {
		curator = CuratorFrameworkFactory
			.newClient("localhost:2181", 10000, 10000, new RetryNTimes(3, 3000) );
        
		curator.start();
		curator.getZookeeperClient().blockUntilConnectedOrTimedOut();
			
		List<String> children = curator.getChildren().usingWatcher(new CuratorWatcher() {
			@Override
			public void process(WatchedEvent event) throws Exception {
				EventType eventType = event.getType();
				String path = event.getPath();

				switch (eventType) {
				case NodeChildrenChanged:
					loadServices(curator.getChildren().forPath(path));
					break;
				default:
					break;
				}
				
			}
		}).forPath(ROOT_NODE);
				
		loadServices(children); 
		
		Thread.sleep(20000);
	}

	private static void loadServices(List<String> children) throws Exception {
		Set<String> services = new HashSet<>();
		
		services.addAll(children);
		
		for (String service : services) {
			listChildrenUsingWatcher(ROOT_NODE + "/" + service + "/consumers");
			listChildrenUsingWatcher(ROOT_NODE + "/" + service + "/providers");
		}
	}
	
	static public List<String> listChildrenUsingWatcher(String node) {
		List<String> children = null;
		try {
			Stat stat = curator.checkExists().forPath(node);
			if (stat != null) {
				BackgroundPathable<List<String>> childrenBuilder = 
						curator.getChildren().usingWatcher(new CuratorWatcher() {

							@Override
							public void process(WatchedEvent event) throws Exception {
								System.out.println(event.getPath());
							}
							
						});
				children = childrenBuilder.forPath(node);
				System.out.println(node + ": " + children.size());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return children;
	}
}
