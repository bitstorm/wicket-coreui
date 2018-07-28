/**
 * 
 */
package it.adelbene.ui.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import it.adelbene.DubboAwsomeMonitorApp;
import it.adelbene.dubbo.domain.DubboService;
import it.adelbene.zookeeper.DubboZkManager;

/**
 * @author andrea.delbene
 *
 */
public class ApplicationsModel extends LoadableDetachableModel<List<DubboService>>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7816560438702860073L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.model.LoadableDetachableModel#load()
	 */
	@Override
	protected List<DubboService> load()
	{
		DubboAwsomeMonitorApp wicketApp = DubboAwsomeMonitorApp.get();
		DubboZkManager dubboManager = wicketApp.getDubboZkManager();

		return new ArrayList<>(dubboManager.getAppServices().values());
	}
}
