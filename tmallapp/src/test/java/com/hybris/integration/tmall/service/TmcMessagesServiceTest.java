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

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.model.TmcMessage;
import com.hybris.integration.service.tmall.TmcMessagesService;
import com.hybris.integration.tmall.BaseJUnitTest;
import com.hybris.integration.util.CommonUtils;


/**
 * TmcMessagesServiceTest
 */
public class TmcMessagesServiceTest extends BaseJUnitTest
{

	/**
	 * TmcMessage
	 */
	public TmcMessage tm;

	@Autowired
	private TmcMessagesService tmService;

	/**
	 * setUp
	 */
	@BeforeClass
	public void setUp()
	{
		final String jsonContent = getProfileContentByName("TmcMessageProfile.json");

		tm = CommonUtils.getGsonByBuilder().fromJson(jsonContent, TmcMessage.class);

	}

	/**
	 * testSaveOrUpdateTmcMessage
	 */
	@Test(enabled = false)
	public void testSaveOrUpdateTmcMessage()
	{
		try
		{
			tmService.saveTmcMessage(tm);
			final TmcMessage tempTm = tmService.getTmcMessage(tm.getIntegrationId());

			Assert.assertEquals(tm.getCurrency(), tempTm.getCurrency());

			tempTm.setCurrency("RMB");
			tmService.saveTmcMessage(tempTm);
			final TmcMessage tempTm2 = tmService.getTmcMessage(tempTm.getIntegrationId());

			Assert.assertEquals(tempTm.getCurrency(), tempTm2.getCurrency());
		}
		catch (final TmallAppException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * getTmcMessage
	 */
	@Test(dependsOnMethods =
	{ "testSaveOrUpdateTmcMessage" }, enabled = false)
	public void getTmcMessage()
	{
		try
		{
			final TmcMessage tempTm = tmService.getTmcMessage(tm.getIntegrationId());
			Assert.assertEquals(tempTm.getCurrency(), "RMB");
		}
		catch (final TmallAppException e)
		{
			Assert.fail(e.getMessage());
		}
	}
}
