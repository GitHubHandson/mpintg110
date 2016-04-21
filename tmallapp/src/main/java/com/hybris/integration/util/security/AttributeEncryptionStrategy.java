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
package com.hybris.integration.util.security;

/**
 * AttributeEncryptionStrategy
 */
public abstract interface AttributeEncryptionStrategy
{
	/**
	 * @param paramString
	 * @return String
	 * @throws Exception
	 */
	public abstract String encrypt(String paramString) throws Exception;

	/**
	 * @param paramString
	 * @return String
	 * @throws Exception
	 */
	public abstract String decrypt(String paramString) throws Exception;

	/**
	 * @return boolean
	 */
	public abstract boolean isEnabled();
}
