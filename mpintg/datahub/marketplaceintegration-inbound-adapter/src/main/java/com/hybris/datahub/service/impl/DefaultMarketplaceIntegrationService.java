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
package com.hybris.datahub.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import com.hybris.datahub.service.MarketplaceIntegrationService;


/**
 * DefaultMarketplaceIntegrationService
 */
public class DefaultMarketplaceIntegrationService implements MarketplaceIntegrationService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMarketplaceIntegrationService.class);

	private MessageChannel rawFragmentInputChannel;
	private Map<String, String> itemFeedMap;
	private static String DEFAULT_FEED = "DEFAULT_FEED";

	@Override
	public void processRawItem(final String rawItemType, final Map<String, String> csv)
	{
		if (null == csv)
		{
			return;
		}
		final List<Map<String, String>> rawFragments = new LinkedList<Map<String, String>>();
		rawFragments.add(csv);

		final boolean result = rawFragmentInputChannel.send(new GenericMessage<List<Map<String, String>>>(rawFragments,
				constructMessageHeader(rawItemType, getFeedName(rawItemType))));
		if (LOG.isInfoEnabled())
		{
			LOG.info("Process result : " + result + ", item type :" + rawItemType + " fragment: " + csv.toString());
		}
	}

	private MessageHeaders constructMessageHeader(final String rawItemType, final String feedName)
	{
		final Map<String, Object> header = new HashMap<String, Object>();
		header.put("itemType", rawItemType);
		header.put("feedName", feedName);
		return new MessageHeaders(header);
	}

	/**
	 * @param rawFragmentInputChannel
	 *           the rawFragmentInputChannel to set
	 */
	public void setRawFragmentInputChannel(final MessageChannel rawFragmentInputChannel)
	{
		this.rawFragmentInputChannel = rawFragmentInputChannel;
	}


	@Override
	public void processRawItem(String rawItemType, List<Map<String, String>> csv)
	{
		if (null == csv)
		{
			return;
		}

		final List<Map<String, String>> rawFragments = new LinkedList<Map<String, String>>();
		rawFragments.addAll(csv);

		final boolean result = rawFragmentInputChannel.send(new GenericMessage<List<Map<String, String>>>(rawFragments,
				constructMessageHeader(rawItemType, getFeedName(rawItemType))));
		if (LOG.isInfoEnabled())
		{
			LOG.info("Process result : " + result + ", item type :" + rawItemType + " fragment: " + csv.toString());
		}
	}

	private String getFeedName(String rawItemType)
	{
		return itemFeedMap.containsKey(rawItemType) ? itemFeedMap.get(rawItemType) : itemFeedMap.get(DEFAULT_FEED);
	}

	@Required
	public void setItemFeedMap(Map<String, String> itemFeedMap)
	{
		this.itemFeedMap = itemFeedMap;
	}
}
