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
package com.hybris.integration.tmall.others;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hybris.integration.tmall.BaseJUnitTest;
import com.hybris.integration.util.security.AttributeEncryptionStrategy;


/**
 * TestAttributeEncryptionStrategy
 */
public class TestAttributeEncryptionStrategy extends BaseJUnitTest
{

	private static final Logger LOGGER = LoggerFactory.getLogger(TestAttributeEncryptionStrategy.class);

	@Autowired
	private AttributeEncryptionStrategy encryptionTools;
	private String rawContent;

	/**
	 * setUp
	 */
	@BeforeClass
	public void setUp()
	{
		rawContent = "test encryption";
	}

	/**
	 * testEncryptionAndDecryption
	 */
	@Test(enabled = false)
	public void testEncryptionAndDecryption()
	{
		try
		{
			LOGGER.info("Raw content:" + rawContent);
			final String afterEncryption = encryptionTools.encrypt(rawContent);
			LOGGER.info("After the content is encrypted:" + afterEncryption);

			final String afterDecryption = encryptionTools.decrypt(afterEncryption);

			LOGGER.info("After the content is decrypted:" + afterDecryption);
			Assert.assertEquals(rawContent, afterDecryption);
		}
		catch (final Exception e)
		{
			Assert.fail(e.getMessage(), e);
		}
	}
}
