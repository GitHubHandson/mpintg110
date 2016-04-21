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
package com.hybris.integration.tmall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hybris.integration.model.AccessToken;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.util.security.AttributeEncryptionStrategy;


/**
 * BaseJUnitTest
 */
@ContextConfiguration(
{ "classpath*:context/*.xml" })
@WebAppConfiguration(value = "src/main/webapp")
public class BaseJUnitTest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseJUnitTest.class);

	@Autowired
	private AttributeEncryptionStrategy encryptionTools;

	/**
	 * getProfileContentByName
	 *
	 * @param profileName
	 * @return String
	 */
	public String getProfileContentByName(final String profileName)
	{
		final String filePath = BaseJUnitTest.class.getResource("").getPath() + "/" + profileName;
		String content = "";
		try
		{
			content = encryptionTools.decrypt(CommonUtils.readFile(filePath));
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			return "";
		}
		return content;
	}

	/**
	 * initAccessToken
	 *
	 * @return AccessToken
	 */
	public AccessToken initAccessToken()
	{
		final String jsonContent = getProfileContentByName("AccessTokenProfile.json");

		final AccessToken accessToken = CommonUtils.getGsonByBuilder().fromJson(jsonContent, AccessToken.class);

		return accessToken;
	}

}
