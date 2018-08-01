/**
 * Copyright 2006-2015 handu.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.adelbene.dubbo.domain;

import java.io.Serializable;
import java.util.UUID;

/**
 * DubboService
 *
 * @author Jinkai.Ma
 */
public class DubboService implements Serializable
{

	private String name;
	private String application;
	private String organization;
	private String owner;
	private int providerCount;
	private int consumerCount;
	private final UUID id; 

	/**
	 * 
	 */
	public DubboService()
	{
		id = UUID.randomUUID();
	}
	
	public DubboService(String name, String application)
	{
		this();
		this.name = name;
		this.application = application;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getApplication()
	{
		return application;
	}

	public void setApplication(String application)
	{
		this.application = application;
	}

	public String getOrganization()
	{
		return organization;
	}

	public void setOrganization(String organization)
	{
		this.organization = organization;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public int getProviderCount()
	{
		return providerCount;
	}

	public void setProviderCount(int providerCount)
	{
		this.providerCount = providerCount;
	}

	public int getConsumerCount()
	{
		return consumerCount;
	}

	public void setConsumerCount(int consumerCount)
	{
		this.consumerCount = consumerCount;
	}

	public int increaseConsumersCount()
	{
		return ++consumerCount;
	}

	public int increaseProvidersCount()
	{
		return ++providerCount;
	}

	public int decreaseConsumersCount()
	{
		return --consumerCount;
	}

	public int decreaseProvidersCount()
	{
		return --providerCount;
	}

	@Override
	public String toString()
	{
		return "DubboService [name=" + name + ", application=" + application + ", organization=" +
			organization + ", owner=" + owner + ", providerCount=" + providerCount +
			", consumerCount=" + consumerCount + "]";
	}


}
