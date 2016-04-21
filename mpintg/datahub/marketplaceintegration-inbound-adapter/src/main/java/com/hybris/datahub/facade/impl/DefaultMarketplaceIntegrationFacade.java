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
package com.hybris.datahub.facade.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.datahub.dto.item.ResultData;
import com.hybris.datahub.exception.DataHubException;
import com.hybris.datahub.facade.MarketplaceIntegrationFacade;
import com.hybris.datahub.service.MarketplaceIntegrationHandler;
import com.hybris.datahub.util.ResponseCode;


/**
 * DefaultMarketplaceIntegrationFacade
 */
public class DefaultMarketplaceIntegrationFacade implements MarketplaceIntegrationFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMarketplaceIntegrationFacade.class);
	private Map<String, MarketplaceIntegrationHandler> marketplaceIntegrationHandlers;

	@Override
	public ResultData process(final String type, final String json)
	{
		final ResultData result = new ResultData();
		MarketplaceIntegrationHandler marketplaceIntegrationHandler = marketplaceIntegrationHandlers.get(type);
		if (null == marketplaceIntegrationHandler)
		{
			LOG.error("can not find handler for type " + type + ",json=" + json);
			throw new DataHubException(ResponseCode.DATAHUB_MISSING_TYPE.getCode(),
					"can not find handler for type " + type + ",json=" + json);
		}
		
		result.setNumberProcessed(marketplaceIntegrationHandler.handle(json));
		
		return result;
	}

	/**
	 * @param marketplaceIntegrationHandlers
	 *           the marketplaceIntegrationHandlers to set
	 */
	public void setMarketplaceIntegrationHandlers(final Map<String, MarketplaceIntegrationHandler> marketplaceIntegrationHandlers)
	{
		this.marketplaceIntegrationHandlers = marketplaceIntegrationHandlers;
	}

}
