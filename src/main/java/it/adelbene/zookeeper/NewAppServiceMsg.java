/**
 * 
 */
package it.adelbene.zookeeper;

import it.adelbene.dubbo.domain.DubboService;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

/**
 * @author andrea.delbene
 *
 */
public class NewAppServiceMsg implements IWebSocketPushMessage
{

	private final DubboService service;

	/**
	 * @param service
	 */
	public NewAppServiceMsg(DubboService service)
	{
		this.service = service;		
	}

	/**
	 * @return the service
	 */
	public DubboService getService()
	{
		return service;
	}
}
