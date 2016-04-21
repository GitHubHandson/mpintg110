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
package com.hybris.integration.service.tmall.impl;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.model.AccessToken;
import com.hybris.integration.service.datahub.DataHubAuthService;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.util.security.AttributeEncryptionStrategy;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.internal.util.WebUtils;


/**
 * BaseServiceImpl
 */
public abstract class BaseServiceImpl
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenServiceImpl.class);

	@Value("${api.server.url}")
	private String apiServerUrl;

	@Value("${oauth.redirect.url}")
	private String oauthRedirectUrl;

	@Value("${persistence.url}")
	private String persistenceUrl;

	@Value("${oauth.server.url}")
	private String oauthServerUrl;

	@Autowired
	private DataHubAuthService dhAuthService;

	@Autowired
	private AttributeEncryptionStrategy encryptionTools;

	/**
	 * @param integrationId
	 * @return DefaultTaobaoClient instance
	 * @throws TmallAppException
	 */
	protected DefaultTaobaoClient getClient(final String integrationId) throws TmallAppException
	{
		final AccessToken accesstoken = get(integrationId);
		return new DefaultTaobaoClient(apiServerUrl, accesstoken.getAppkey(), accesstoken.getSecret());
	}

	/**
	 * @param accessToken
	 * @throws TmallAppException
	 */
	protected void save(final AccessToken accessToken) throws TmallAppException
	{
		final String persistence = persistenceUrl + accessToken.getIntegrationId();

		String content = CommonUtils.getGsonByBuilder().toJson(accessToken);
		try
		{
			content = encryptionTools.encrypt(content);
		}
		catch (final Exception e)
		{
			LOGGER.error("AES encryption failure:" + e.getMessage(), e);
			throw new TmallAppException(ResponseCode.AES_ENCRYPTION_ERROR.getCode(), "AES encryption failure:" + e.getMessage());
		}
		CommonUtils.writeFile(persistence, content);
	}

	/**
	 * @param integrationId
	 * @return AccessToken Object
	 * @throws TmallAppException
	 */
	protected AccessToken get(final String integrationId) throws TmallAppException
	{
		String tokenJson = CommonUtils.readFile(persistenceUrl + integrationId + ".json");
		AccessToken accessToken = null;
		if (StringUtils.isNotEmpty(tokenJson))
		{
			try
			{
				tokenJson = encryptionTools.decrypt(tokenJson);
			}
			catch (final Exception e)
			{
				LOGGER.error("AES decryption failure:" + e.getMessage(), e);
				throw new TmallAppException(ResponseCode.AES_ENCRYPTION_ERROR.getCode(), "AES decryption failure.");
			}
			accessToken = CommonUtils.getGsonByBuilder().fromJson(tokenJson, AccessToken.class);

			checkTheTokenIsValidAndUpdateAccessToken(accessToken);
		}
		else
		{
			LOGGER.error("The [" + integrationId + "] does not exist");
			throw new TmallAppException(ResponseCode.OBJECT_NOT_FOUND.getCode(), "The [" + integrationId + "] does not exist");
		}
		return accessToken;
	}

	/**
	 * @param code
	 * @param appkey
	 * @param secret
	 * @return AccessToken
	 * @throws IOException
	 */
	protected AccessToken get(final String code, final String appkey, final String secret) throws IOException
	{
		final String url = oauthServerUrl + "/token";
		final Map<String, String> props = new HashMap<String, String>();
		props.put("grant_type", "authorization_code");
		props.put("code", code);
		props.put("client_id", appkey);
		props.put("client_secret", secret);
		props.put("redirect_uri", oauthRedirectUrl);
		props.put("view", "web");
		String s = "";
		try
		{

			LOGGER.trace("The response body is : " + s);
			s = WebUtils.doPost(url, props, 30000, 30000);
			s = URLDecoder.decode(s, "UTF-8");
			LOGGER.trace("Appkey is : " + appkey);
			LOGGER.trace("The response body is : " + s);
		}
		catch (final Exception e)
		{
			LOGGER.trace("Get access token failure:" + e.getMessage());
			LOGGER.trace("Try again!");
			s = WebUtils.doPost(url, props, 30000, 30000);

			LOGGER.trace("The response body is : " + s);
		}
		return CommonUtils.getGsonByBuilder().fromJson(s, AccessToken.class);
	}

	/**
	 * @param appkey
	 * @param secret
	 * @param refreshToken
	 * @return AccessToken
	 * @throws IOException
	 */
	protected AccessToken refresh(final String appkey, final String secret, final String refreshToken) throws IOException
	{
		final String url = oauthServerUrl + "/token";
		final Map<String, String> props = new HashMap<String, String>();
		props.put("grant_type", "refresh_token");
		props.put("client_id", appkey);
		props.put("client_secret", secret);
		props.put("refresh_token", refreshToken);
		String s = "";
		try
		{
			s = WebUtils.doPost(url, props, 30000, 30000);
			s = URLDecoder.decode(s, "UTF-8");
			LOGGER.trace("Appkey is :" + appkey);
			LOGGER.trace("The response body is : " + s);
		}
		catch (final Exception e)
		{
			LOGGER.trace("Refresh access token failure:" + e.getMessage());
			LOGGER.trace("Try again!");
			s = WebUtils.doPost(url, props, 30000, 30000);

			LOGGER.trace("The response body is : " + s);
		}
		return CommonUtils.getGsonByBuilder().fromJson(s, AccessToken.class);
	}

	/**
	 * @param integrationId
	 * @return accesstoken
	 * @throws TmallAppException
	 */
	protected String getToken(final String integrationId) throws TmallAppException
	{
		return get(integrationId).getAccessToken();
	}

	private void checkTheTokenIsValidAndUpdateAccessToken(final AccessToken accessToken) throws TmallAppException
	{
		final long nowTimeStamp = System.currentTimeMillis();

		//Whether the current token expired
		if (nowTimeStamp > accessToken.getExpireTime())
		{
			// Default refresh failed
			boolean refreshFlag = true;

			LOGGER.trace("Refresh token valid time:" + new Date(accessToken.getRefreshTokenValidTime()));
			//Whether the current refresh token expired
			if (nowTimeStamp < accessToken.getRefreshTokenValidTime())
			{
				try
				{
					final AccessToken newAccessToken = refresh(accessToken.getAppkey(), accessToken.getSecret(),
							accessToken.getRefreshToken());

					final String[] ignoreProperties = new String[]
					{ "appkey", "secret", "integrationId", "authorized", "marketplaceStoreId" };

					BeanUtils.copyProperties(newAccessToken, accessToken, ignoreProperties);

					this.save(accessToken);
					refreshFlag = false;
				}
				catch (final Exception e)
				{
					LOGGER.error(e.getMessage(), e);
				}
			}
			//If refresh fails, it sends a message notification token has expired
			if (refreshFlag)
			{
				accessToken.setAuthorized("false");
				accessToken.setSecret(null);
				dhAuthService.saveAuthInfo(accessToken);

				LOGGER.error("Token has been completely invalid, please re-login authorization.");
				throw new TmallAppException(ResponseCode.OBJECT_EXPIRED.getCode(),
						"Token has been completely invalid, please re-login authorization.");
			}
		}
	}
}
