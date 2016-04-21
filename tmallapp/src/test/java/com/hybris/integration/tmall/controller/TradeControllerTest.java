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
package com.hybris.integration.tmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hybris.integration.model.AccessToken;
import com.hybris.integration.tmall.BaseJUnitTest;


/**
 * TradeControllerTest
 */
public class TradeControllerTest extends BaseJUnitTest
{

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private AccessToken accessToken;

	/**
	 * setUp
	 */
	@BeforeClass
	public void setUp()
	{
		mockMvc = webAppContextSetup(wac).build();
		accessToken = initAccessToken();
	}

	/**
	 * initialazeorders
	 *
	 * @throws Exception
	 */
	@Test(enabled = false)
	public void initialazeorders() throws Exception
	{

		final String requestBody = "{\"batchSize\": 200,\"status\":\"WAIT_BUYER_PAY\",\"startCreated\":\"2015-10-12 00:00:00\",\"endCreated\":\"2015-10-13 23:59:59\"}";
		final MvcResult result = mockMvc
				.perform(
						post("/biz/trade/initialazeorders/{integrationId}", accessToken.getIntegrationId())
								.contentType(MediaType.APPLICATION_JSON).content(requestBody).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		Assert.assertEquals(200, result.getResponse().getStatus());
	}

}
