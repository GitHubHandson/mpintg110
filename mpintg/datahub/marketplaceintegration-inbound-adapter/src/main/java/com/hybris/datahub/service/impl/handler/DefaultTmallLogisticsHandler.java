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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.service.JsonService;
import com.hybris.datahub.service.MarketplaceIntegrationHandler;
import com.hybris.datahub.service.MarketplaceIntegrationService;


/**
 * 
 */
public class DefaultTmallLogisticsHandler implements MarketplaceIntegrationHandler
{

	private String rawItemType;
	private MarketplaceIntegrationService marketplaceIntegrationService;
	private JsonService jsonService;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultTmallLogisticsHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hybris.datahub.service.MarketplaceIntegrationHandler#handle(java.lang.String)
	 */
	@Override
	public int handle(String context)
	{
		LOG.debug("Order Consignment: " + context);

		final String json = context;
		marketplaceIntegrationService.processRawItem(rawItemType, jsonService.parse(json).get(0));
		return 1;

	}

	/**
	 * @param marketplaceIntegrationService
	 *           the marketplaceIntegrationService to set
	 */
	@Required
	public void setMarketplaceIntegrationService(MarketplaceIntegrationService marketplaceIntegrationService)
	{
		this.marketplaceIntegrationService = marketplaceIntegrationService;
	}

	/**
	 * @param jsonService
	 *           the jsonService to set
	 */
	@Required
	public void setJsonService(JsonService jsonService)
	{
		this.jsonService = jsonService;
	}

	public void setRawItemType(String rawItemType)
	{
		this.rawItemType = rawItemType;
	}

}
