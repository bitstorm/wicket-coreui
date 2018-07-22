package it.adelbene.dubbo.demo.provider;

import it.adelbene.dubbo.demo.DemoService;

public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		 return "Hello " + name;
	}

}
