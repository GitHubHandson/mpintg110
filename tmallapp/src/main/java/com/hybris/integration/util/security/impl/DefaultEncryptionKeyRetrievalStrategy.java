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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.integration.util.security.EncryptionKeyRetrievalStrategy;


/**
 * DefaultEncryptionKeyRetrievalStrategy
 */
public class DefaultEncryptionKeyRetrievalStrategy implements EncryptionKeyRetrievalStrategy<SecretKeySpec>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEncryptionKeyRetrievalStrategy.class);
	private static final String ENCRYPTION_ALGORITHM = "AES";
	private static final int KEY_SIZE_IN_CHARS = 32;
	private String keyFilePath;
	private SecretKeySpec keySpec;

	public SecretKeySpec getKey()
	{
		try
		{
			return ((this.keySpec == null) ? loadKeyFromFile() : this.keySpec);
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	private SecretKeySpec loadKeyFromFile() throws Exception
	{
		try
		{
			// LOGGER.info("Looking for encryption key file in path: {}",
			// this.keyFilePath);
			final Path keyFile = Paths.get(this.keyFilePath, new String[0]);
			if (keyFile.toFile().canRead())
			{
				return getKeySpecFromPath(keyFile);
			}

			return tryToLoadKeyFromClasspath();
		}
		catch (final IOException e)
		{
			LOGGER.error("Error trying to load encryption key from file", e);
		}
		return null;
	}

	private SecretKeySpec getKeySpecFromPath(final Path path) throws IOException, Exception
	{
		final InputStream stream = Files.newInputStream(path, new OpenOption[0]);
		try
		{
			final SecretKeySpec localSecretKeySpec = convertStreamToKey(stream);
			return localSecretKeySpec;
		}
		catch (final Throwable localThrowable4)
		{
			LOGGER.error(localThrowable4.getMessage());
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (final IOException e)
				{
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	private SecretKeySpec tryToLoadKeyFromClasspath() throws IOException, Exception
	{
		final ClassLoader classloader = getClassLoader();

		if (classloader != null)
		{
			final InputStream stream = classloader.getResourceAsStream(this.keyFilePath);
			final Throwable localThrowable3 = null;
			try
			{
				final SecretKeySpec localSecretKeySpec = convertStreamToKey(stream);
				return localSecretKeySpec;
			}
			catch (final Throwable localThrowable4)
			{
				LOGGER.error(localThrowable4.getMessage());
			}
			finally
			{
				if (stream != null)
				{
					if (localThrowable3 != null)
					{
						try
						{
							stream.close();
						}
						catch (final Throwable localThrowable2)
						{
							localThrowable3.addSuppressed(localThrowable2);
						}
					}
					else
					{
						try
						{
							stream.close();
						}
						catch (final IOException e)
						{
							LOGGER.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		LOGGER.error("Error loading encryption key from classpath");
		return null;
	}

	ClassLoader getClassLoader()
	{
		return DefaultEncryptionKeyRetrievalStrategy.class.getClassLoader();
	}

	private SecretKeySpec convertStreamToKey(final InputStream stream) throws IOException, Exception
	{
		if (stream != null)
		{
			final byte[] buffer = new byte[KEY_SIZE_IN_CHARS];
			IOUtils.read(stream, buffer, 0, KEY_SIZE_IN_CHARS);
			final String data = new String(buffer);
			return keyStringToSecretKeySpec(data);
		}
		LOGGER.error("Cannot load encryption key from path: {}", this.keyFilePath);
		return null;
	}

	private SecretKeySpec keyStringToSecretKeySpec(final String data) throws Exception
	{
		final byte[] encoded = Hex.decodeHex(data.toCharArray());
		this.keySpec = new SecretKeySpec(encoded, ENCRYPTION_ALGORITHM);
		return this.keySpec;
	}


	/**
	 * @param keyFilePath
	 */
	@Required
	public void setKeyFilePath(final String keyFilePath)
	{
		this.keyFilePath = keyFilePath;
	}
}
