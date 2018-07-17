/**
 * 
 */
package it.adelbene.ui.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.dubbo.common.URL;
import org.apache.wicket.model.LoadableDetachableModel;

import it.adelbene.DubboAwsomeMonitorApp;
import it.adelbene.dubbo.RegisterListener;
import it.adelbene.dubbo.domain.DubboApplication;

/**
 * @author andrea.delbene
 *
 */
public class ApplicationsModel extends LoadableDetachableModel<List<DubboApplication>>
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
	protected List<DubboApplication> load()
	{
		DubboAwsomeMonitorApp wicketApp = DubboAwsomeMonitorApp.get();
		RegisterListener dubboListener = wicketApp.getDubboListener();

		Set<String> applications = dubboListener.getApplications();
		List<DubboApplication> applicationsList = new ArrayList<DubboApplication>();
		DubboApplication dubboApplication;
		if (applications != null && applications.size() > 0)
		{
			for (String application : applications)
			{
				dubboApplication = new DubboApplication();
				dubboApplication.setName(application);
				List<URL> providers = dubboListener.getProvidersByApplication(application);
				List<URL> consumers = dubboListener.getConsumersByApplication(application);

				if ((providers != null && providers.size() > 0) ||
					(consumers != null && consumers.size() > 0))
				{
					URL url = (providers != null && providers.size() > 0)
						? providers.iterator().next() : consumers.iterator().next();
					dubboApplication.setName(application);
					dubboApplication.setOwner(url.getParameter("owner", ""));
					dubboApplication.setOrganization(
						(url.hasParameter("organization") ? url.getParameter("organization") : ""));
				}

				dubboApplication.setProviderCount(providers == null ? 0 : providers.size());

				dubboApplication.setConsumerCount(consumers == null ? 0 : consumers.size());

				Set<String> efferents = dubboListener.getDependencies(application, false);
				dubboApplication.setEfferentCount(efferents == null ? 0 : efferents.size());

				Set<String> afferents = dubboListener.getDependencies(application, true);
				dubboApplication.setAfferentCount(afferents == null ? 0 : afferents.size());

				applicationsList.add(dubboApplication);
			}
		}

		return applicationsList;
	}
}
