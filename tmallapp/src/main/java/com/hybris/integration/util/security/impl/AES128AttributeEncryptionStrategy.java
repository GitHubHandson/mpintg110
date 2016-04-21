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
package com.hybris.integration.util.security.impl;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.integration.util.security.AttributeEncryptionStrategy;
import com.hybris.integration.util.security.EncryptionKeyRetrievalStrategy;


/**
 * AES128AttributeEncryptionStrategy
 */
public class AES128AttributeEncryptionStrategy implements AttributeEncryptionStrategy
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEncryptionKeyRetrievalStrategy.class);
	private static final String ENCRYPTION_ALGORITHM = "AES";
	private static final String ENCRYPTION_CHECK = "encryption check";
	private EncryptionKeyRetrievalStrategy<SecretKeySpec> keyRetrievalStrategy;


	@Override
	public boolean isEnabled()
	{
		return (Boolean.valueOf((getKeySpec() != null) && ((isEncryptionAndDecryptionWorking()))).booleanValue());
	}

	public String encrypt(final String s) throws Exception
	{
		if (StringUtils.isNotEmpty(s))
		{
			final Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			final SecretKeySpec keySpec = getKeySpec();
			cipher.init(1, keySpec);

			final byte[] encrypted = cipher.doFinal(s.getBytes());
			final byte[] encoded = new Hex().encode(encrypted);
			return new String(encoded);
		}
		return s;
	}

	public String decrypt(final String c) throws Exception
	{
		if (StringUtils.isNotEmpty(c))
		{
			final Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			final SecretKeySpec keySpec = getKeySpec();

			cipher.init(2, keySpec);

			final byte[] decoded = new Hex().decode(c.getBytes());
			final byte[] decrypted = cipher.doFinal(decoded);
			return new String(decrypted);
		}
		return c;
	}

	private SecretKeySpec getKeySpec()
	{
		return (this.keyRetrievalStrategy.getKey());
	}

	private boolean isEncryptionAndDecryptionWorking()
	{
		try
		{
			final String encrypted = encrypt(ENCRYPTION_CHECK);
			final String decrypted = decrypt(encrypted);
			return ENCRYPTION_CHECK.equals(decrypted);
		}
		catch (final Exception e)
		{
			LOGGER.error("Error occurred when trying to encrypt or decrypt", e);
		}
		return false;
	}

	/**
	 * @param keyRetrievalStrategy
	 */
	@Required
	public void setKeyRetrievalStrategy(final EncryptionKeyRetrievalStrategy<SecretKeySpec> keyRetrievalStrategy)
	{
		this.keyRetrievalStrategy = keyRetrievalStrategy;
	}

}
