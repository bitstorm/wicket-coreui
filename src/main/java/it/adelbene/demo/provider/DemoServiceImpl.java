package it.adelbene.demo.provider;

import it.adelbene.demo.DemoService;

public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		 return "Hello " + name;
	}

}
