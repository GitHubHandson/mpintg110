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
package com.hybris.integration.service.datahub.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.hybris.integration.model.AccessToken;
import com.hybris.integration.service.datahub.DataHubTradeService;
import com.hybris.integration.service.datahub.bean.DatahubTradeRequest;
import com.hybris.integration.service.tmall.AccessTokenService;
import com.hybris.integration.util.CommonUtils;


/**
 * Datahub OrderServiceImpl
 */
@Service("datahub.orderServiceImpl")
public class DataHubTradeServiceImpl extends BaseServiceImpl implements DataHubTradeService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DataHubTradeServiceImpl.class);

	@Value("${datahub.trades.api}")
	private String datahubTradesAPI;

	@Autowired
	private AccessTokenService accessTokenService;

	@Override
	public Boolean saveOrders(final DatahubTradeRequest orderRequest, final String integrationId)
	{
		JsonObject responseResults = null;
		try
		{
			final AccessToken token = accessTokenService.get(integrationId);
			orderRequest.setMarketplaceStoreId(token.getMarketplaceStoreId());
			orderRequest.setSuffix(tmallStoreCustomSuffix);

			String url = datahubServerUrl + datahubTradesAPI;
			final String body = CommonUtils.getGsonByBuilder(false).toJson(orderRequest);
			responseResults = restTemplateUtil.post(url, body);
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		String status = null;

		if (responseResults != null)
		{
			status = responseResults.get("code").getAsString();
		}
		return "0".equals(status) ? true : false;
	}

}
