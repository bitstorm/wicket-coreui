package it.adelbene.config;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.websocket.server.ServerContainer;

public class WebSocketInitializer implements ServletContainerInitializer
{

	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException
	{
		ServerContainer wsContainer = (ServerContainer)ctx
			.getAttribute("javax.websocket.server.ServerContainer");
		//wsContainer.setDefaultMaxSessionIdleTimeout(3600000);
	}

}
