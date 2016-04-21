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
package com.hybris.datahub.service.impl.handler;

import com.hybris.datahub.service.JsonService;
import com.hybris.datahub.service.MarketplaceIntegrationHandler;
import com.hybris.datahub.service.MarketplaceIntegrationService;


/**
 * DefaultJsonHandler
 */
public class DefaultJsonHandler implements MarketplaceIntegrationHandler
{
	private String rawItemType;
	private JsonService jsonService;
	private MarketplaceIntegrationService marketplaceIntegrationService;

	@Override
	public int handle(final String context)
	{
		final String json = context;
		marketplaceIntegrationService.processRawItem(rawItemType, jsonService.parse(json).get(0));
		return 1;
	}

	/**
	 * @param jsonService
	 *           the jsonService to set
	 */
	public void setJsonService(final JsonService jsonService)
	{
		this.jsonService = jsonService;
	}

	/**
	 * @param marketplaceIntegrationService
	 *           the marketplaceIntegrationService to set
	 */
	public void setMarketplaceIntegrationService(final MarketplaceIntegrationService marketplaceIntegrationService)
	{
		this.marketplaceIntegrationService = marketplaceIntegrationService;
	}

	/**
	 * @param rawItemType
	 *           the rawItemType to set
	 */
	public void setRawItemType(final String rawItemType)
	{
		this.rawItemType = rawItemType;
	}

}
