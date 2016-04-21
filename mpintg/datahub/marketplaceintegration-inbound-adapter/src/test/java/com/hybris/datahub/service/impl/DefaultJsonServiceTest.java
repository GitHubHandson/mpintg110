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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.datahub.service.JsonService;


/**
 * DefaultJsonServiceTest
 */
public class DefaultJsonServiceTest
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultJsonServiceTest.class);

	private static final String SAMPLE_TMALL_AUTH_JSON = "SampleTmallAuth.json";
	private static final String SAMPLE_TMALL_TRADES_JSON = "SampleTmallTrades.json";

	private JsonService service;

	/**
	 * Test set up
	 */
	@Before
	public void setUp()
	{
		service = new DefaultJsonService();
	}

	/**
	 * Test tear down
	 */
	@After
	public void tearDown()
	{
	}

	/**
	 * convertTmallAuthJson
	 */
	@Test
	public void convertTmallAuthJson()
	{
		final Class<? extends DefaultJsonServiceTest> instance = getClass();
		if (instance != null)
		{
			final ClassLoader classLoader = instance.getClassLoader();
			if (classLoader != null)
			{
				String json = "";
				InputStream resourceAsStream = null;
				try
				{
					resourceAsStream = classLoader.getResourceAsStream(SAMPLE_TMALL_AUTH_JSON);
					json = IOUtils.toString(resourceAsStream);
				}
				catch (final IOException e)
				{
					LOG.error(e.getMessage(), e);
				}
				finally
				{
					if (resourceAsStream != null)
					{
						try
						{
							resourceAsStream.close();
						}
						catch (final IOException e)
						{
							LOG.error(e.getMessage(), e);
						}
					}
				}
				final List<Map<String, String>> csv = service.parse(json);
				final Map<String, String> raw = csv.get(0);

				Assert.assertTrue(raw.containsKey("marketplaceStoreName"));
				Assert.assertTrue(raw.containsKey("integrationId"));
				Assert.assertTrue(raw.containsKey("authorized"));
				Assert.assertEquals("TmallStore", raw.get("marketplaceStoreName"));
				Assert.assertEquals("1234567890", raw.get("integrationId"));
				Assert.assertEquals("true", raw.get("authorized"));
			}
		}
		else
		{
			Assert.fail("Class not found.");
		}
	}

	/**
	 * convertTmallTradesJson
	 *
	 * @throws Exception
	 */
	@Test
	public void convertTmallTradesJson() throws Exception
	{
		final Class<? extends DefaultJsonServiceTest> instance = getClass();
		if (instance != null)
		{
			final ClassLoader classLoader = instance.getClassLoader();
			if (classLoader != null)
			{

				String json = "";
				InputStream resourceAsStream = null;
				try
				{
					resourceAsStream = classLoader.getResourceAsStream(SAMPLE_TMALL_TRADES_JSON);
					json = IOUtils.toString(resourceAsStream);
				}
				catch (final IOException e)
				{
					LOG.error(e.getMessage(), e);
				}
				finally
				{
					if (resourceAsStream != null)
					{
						try
						{
							resourceAsStream.close();
						}
						catch (final IOException e)
						{
							LOG.error(e.getMessage(), e);
						}
					}
				}
				final List<Map<String, String>> csv = service.parse(json);
				final Map<String, String> raw = csv.get(0);

				Assert.assertTrue(raw.containsKey("marketplaceStoreName"));
				Assert.assertTrue(raw.containsKey("currency"));
				Assert.assertTrue(raw.containsKey("productCatalogVersion"));
				Assert.assertTrue(raw.containsKey("trades"));

				Assert.assertEquals("CNY", raw.get("currency"));
				Assert.assertEquals("electronics-cnProductCatalog:Online", raw.get("productCatalogVersion"));

				final Map<String, String> rawTrade = service.parse(raw.get("trades")).get(0);

				Assert.assertEquals("2100703549093", rawTrade.get("numIid"));
				Assert.assertEquals("TRADE_CLOSED_BY_TAOBAO", rawTrade.get("status"));
			}
		}
		else
		{
			Assert.fail("Class not found.");
		}
	}
}
