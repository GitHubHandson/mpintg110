/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.datahub.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.api.event.DataHubEventListener;
import com.hybris.datahub.api.event.DataHubInitializationCompletedEvent;
import com.hybris.datahub.service.DataHubFeedService;
import com.hybris.datahub.validation.ValidationException;


/**
 * Creates event feed after the initialization of the Data Hub.
 */
public class FeedCreationListener implements DataHubEventListener<DataHubInitializationCompletedEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(FeedCreationListener.class);

	private Map<String, String> feedPoolNameMap;
	private DataHubFeedService feedService;

	@Override
	public void handleEvent(final DataHubInitializationCompletedEvent event)
	{
		LOG.debug("Checking for existing of event driven feed");

		feedPoolNameMap.forEach((feedName, poolName) -> {
			if (feedService.findDataFeedByName(feedName) == null)
			{
				try
				{
					LOG.info("Create event driven feed" + feedName + ",and pool " + poolName);
					feedService.createFeed(feedName, poolName, "MANUAL", "MANUAL", "NAMED_POOL",
							"A event feed for composition and publication");
				}
				catch (final ValidationException e)
				{
					LOG.error("Error creating event feed", e);
				}
			}
		});
	}

	@Override
	public Class<DataHubInitializationCompletedEvent> getEventClass()
	{
		return DataHubInitializationCompletedEvent.class;
	}

	@Override
	public boolean executeInTransaction()
	{
		return true;
	}

	/**
	 * @param feedService
	 */
	public void setFeedService(final DataHubFeedService feedService)
	{
		this.feedService = feedService;
	}

	public Map<String, String> getFeedPoolNameMap()
	{
		return feedPoolNameMap;
	}

	@Required
	public void setFeedPoolNameMap(Map<String, String> feedPoolNameMap)
	{
		this.feedPoolNameMap = feedPoolNameMap;
	}


}
