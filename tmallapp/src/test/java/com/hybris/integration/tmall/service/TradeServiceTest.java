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
package com.hybris.integration.tmall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hybris.integration.model.AccessToken;
import com.hybris.integration.service.tmall.TradeService;
import com.hybris.integration.tmall.BaseJUnitTest;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.TradesSoldGetRequest;
import com.taobao.api.response.TradesSoldGetResponse;


/**
 * TradeServiceTest
 */
public class TradeServiceTest extends BaseJUnitTest
{

	@Autowired
	private TradeService tradeService;

	private AccessToken accessToken;

	private static Long tid;

	/**
	 * buildingAccessToken
	 */
	@BeforeClass
	public void buildingAccessToken()
	{
		accessToken = initAccessToken();
	}

	/**
	 * testGetSoldTrades
	 */
	@Test(enabled = false)
	public void testGetSoldTrades()
	{
		final TradesSoldGetRequest req = new TradesSoldGetRequest();
		req.setFields("tid");
		try
		{
			final TradesSoldGetResponse response = tradeService.getSoldTrades(accessToken.getIntegrationId(), req);

			Assert.assertEquals(response.getErrorCode(), null,
					"Pull the order failed, an error message is as follows:" + response.getBody());

			tid = response.getTrades().get(0).getTid();

		}
		catch (final Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * testGetTradeFullInfoByTid
	 */
	@Test(dependsOnMethods =
	{ "testGetSoldTrades" }, enabled = false)
	public void testGetTradeFullInfoByTid()
	{
		Trade trade = null;

		try
		{
			trade = tradeService.getTradeFullInfoByTid(accessToken.getIntegrationId(), tid);

			Assert.assertNotEquals(trade, null, "Get order details failure.");
		}
		catch (final Exception e)
		{
			Assert.fail(e.getMessage());
		}

	}

}
