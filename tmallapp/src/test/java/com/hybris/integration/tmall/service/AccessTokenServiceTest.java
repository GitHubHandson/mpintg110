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
import com.hybris.integration.service.tmall.AccessTokenService;
import com.hybris.integration.tmall.BaseJUnitTest;


/**
 * AccessTokenServiceTest
 */
public class AccessTokenServiceTest extends BaseJUnitTest
{

	private AccessToken accessToken;

	@Autowired
	private AccessTokenService accessTokenService;

	/**
	 * buildingAccessToken
	 */
	@BeforeClass
	public void buildingAccessToken()
	{
		accessToken = initAccessToken();
	}

	/**
	 * testSaveOrUpdate
	 */
	@Test(enabled = false)
	public void testSaveOrUpdate()
	{

		try
		{
			//Save access token information
			accessTokenService.save(accessToken);
			final AccessToken token1 = accessTokenService.get(accessToken.getIntegrationId());

			Assert.assertEquals(token1.getAccessToken(), accessToken.getAccessToken(),
					"Failed to save the access token information.");

			//Update access token value
			token1.setAuthorized("false.");
			accessTokenService.save(token1);
			final AccessToken token2 = accessTokenService.get(token1.getIntegrationId());

			Assert.assertEquals(token2.getAuthorized(), "false.", "Failed to update the access token information.");

		}
		catch (final Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * testGetAccessToken
	 */
	@Test(dependsOnMethods =
	{ "testSaveOrUpdate" }, enabled = false)
	public void testGetAccessToken()
	{
		try
		{
			final AccessToken token = accessTokenService.get(accessToken.getIntegrationId());

			Assert.assertEquals(token.getAuthorized(), "false.");

		}
		catch (final Exception e)
		{
			Assert.fail(e.getMessage());
		}


	}

}
