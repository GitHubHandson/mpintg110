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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.datahub.api.event.CompositionCompletedEvent;
import com.hybris.datahub.api.event.DataHubEventListener;
import com.hybris.datahub.api.event.InitiatePublicationEvent;
import com.hybris.datahub.service.EventPublicationService;
import com.hybris.datahub.service.impl.AbstractPoolEventListener;


/**
 * CompositionCompletedEventListener
 */
public class CompositionCompletedEventListener extends AbstractPoolEventListener implements
		DataHubEventListener<CompositionCompletedEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(CompositionCompletedEventListener.class);
	private String[] targetSystem;

	private EventPublicationService eventPublicationService;

	@Override
	public void handleEvent(final CompositionCompletedEvent event)
	{
		final String poolName = getPoolNameFromId(event.getPoolId());
		LOG.debug("Handling composition completed event for pool : {}", poolName);
		final InitiatePublicationEvent publishEvent = new InitiatePublicationEvent(event.getPoolId(), Arrays.asList(targetSystem));
		eventPublicationService.publishEvent(publishEvent);
	}

	@Override
	public Class<CompositionCompletedEvent> getEventClass()
	{
		return CompositionCompletedEvent.class;
	}

	@Override
	public boolean executeInTransaction()
	{
		return true;
	}

	/**
	 * @param eventPublicationService
	 */
	public void setEventPublicationService(final EventPublicationService eventPublicationService)
	{
		this.eventPublicationService = eventPublicationService;
	}

	/**
	 * @param targetSystem
	 *           the targetSystem to set
	 */
	public void setTargetSystem(final String[] targetSystem)
	{
		this.targetSystem = targetSystem;
	}
}
