package it.adelbene.dubbo.demo.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import it.adelbene.dubbo.demo.DemoService;

public class Consumer {
	public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        DemoService demoService = (DemoService)context.getBean("demoService"); //Obtaining a remote service proxy
        
        while (true) {			
        	String hello = demoService.sayHello("world"); // Executing remote methods 
        	System.out.println( hello ); // Display the call result 
        	Thread.sleep(9000);
        }
	}
}
