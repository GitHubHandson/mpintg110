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
package com.hybris.integration.service.tmall;

import java.io.IOException;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.model.AccessToken;


/**
 * AccessTokenService
 */
public interface AccessTokenService
{

	/**
	 * @param accessToken
	 * @throws TmallAppException
	 */
	public void save(AccessToken accessToken) throws TmallAppException;

	/**
	 * @param integrationId
	 * @return AccessToken
	 * @throws Exception
	 */
	public AccessToken get(String integrationId) throws TmallAppException;

	/**
	 * @param code
	 * @param appkey
	 * @param secret
	 * @return AccessToken
	 * @throws IOException
	 */
	public AccessToken get(String code, String appkey, String secret) throws IOException;

	/**
	 * @param appkey
	 * @param secret
	 * @param refreshToken
	 * @return AccessToken
	 * @throws IOException
	 */
	public AccessToken refresh(String appkey, String secret, String refreshToken) throws IOException;
}
