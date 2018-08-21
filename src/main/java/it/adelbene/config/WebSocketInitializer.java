package it.adelbene.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.server.ServerContainer;

@WebListener
public class WebSocketInitializer implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		ServletContext ctx = sce.getServletContext();
		ServerContainer wsContainer = (ServerContainer)ctx
			.getAttribute("javax.websocket.server.ServerContainer");
		wsContainer.setDefaultMaxSessionIdleTimeout(3600000);
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
	}
}
