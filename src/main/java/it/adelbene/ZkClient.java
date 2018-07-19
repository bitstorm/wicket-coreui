package it.adelbene;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.WatchedEvent;

public class ZkClient {

	public static void main(String[] args) throws Exception {
		CuratorFramework curator =
			CuratorFrameworkFactory
				.newClient("localhost:2181", 10000, 10000, new RetryNTimes(3, 3000) );

		curator.start();
		curator.getZookeeperClient().blockUntilConnectedOrTimedOut();
			
		List<String> children = curator.getChildren().usingWatcher(new CuratorWatcher() {
			
			@Override
			public void process(WatchedEvent arg0) throws Exception {
				System.out.println(arg0);
				
			}
		}).forPath("/dubbo/org.apache.dubbo.monitor.MonitorService/consumers");
		
		children.forEach(System.out::println);
		Thread.sleep(8000);
	}

}
