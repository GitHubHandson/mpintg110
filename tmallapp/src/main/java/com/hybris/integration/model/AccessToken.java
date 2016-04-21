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
package com.hybris.integration.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * AccessToken
 */
public class AccessToken implements Serializable
{

	private static final long serialVersionUID = 4348267604179447597L;
	@Expose
	private String integrationId;
	@Expose
	private String marketplaceStoreId;
	@Expose
	private String authorized = "true";

	@Expose
	private String appkey;

	@Expose
	private String secret;

	@Expose
	@SerializedName("access_token")
	private String accessToken;

	@Expose
	@SerializedName("expire_time")
	private Long expireTime;

	@Expose
	@SerializedName("expires_in")
	private Long expiresIn;

	@Expose
	@SerializedName("r1_expires_in")
	private Long r1ExpiresIn;

	@Expose
	@SerializedName("r1_valid")
	private Long r1Valid;

	@Expose
	@SerializedName("r2_expires_in")
	private Long r2ExpiresIn;

	@Expose
	@SerializedName("r2_valid")
	private Long r2Valid;

	@Expose
	@SerializedName("re_expires_in")
	private Long reExpiresIn;

	@Expose
	@SerializedName("refresh_token")
	private String refreshToken;

	@Expose
	@SerializedName("refresh_token_valid_time")
	private Long refreshTokenValidTime;

	@Expose
	@SerializedName("taobao_user_id")
	private Long taobaoUserId;

	@Expose
	@SerializedName("taobao_user_nick")
	private String taobaoUserNick;

	@Expose
	@SerializedName("token_type")
	private String tokenType;

	@Expose
	@SerializedName("w1_expires_in")
	private Long w1ExpiresIn;

	@Expose
	@SerializedName("w1_valid")
	private Long w1Valid;

	@Expose
	@SerializedName("w2_expires_in")
	private Long w2ExpiresIn;

	@Expose
	@SerializedName("w2_valid")
	private Long w2Valid;


	/**
	 * @return the integrationId
	 */
	public String getIntegrationId()
	{
		return integrationId;
	}

	/**
	 * @param integrationId
	 *           the integrationId to set
	 */
	public void setIntegrationId(final String integrationId)
	{
		this.integrationId = integrationId;
	}

	/**
	 * @return the marketplaceStoreId
	 */
	public String getMarketplaceStoreId()
	{
		return marketplaceStoreId;
	}

	/**
	 * @param marketplaceStoreId
	 *           the marketplaceStoreId to set
	 */
	public void setMarketplaceStoreId(final String marketplaceStoreId)
	{
		this.marketplaceStoreId = marketplaceStoreId;
	}

	/**
	 * @return the appkey
	 */
	public String getAppkey()
	{
		return appkey;
	}

	/**
	 * @param appkey
	 *           the appkey to set
	 */
	public void setAppkey(final String appkey)
	{
		this.appkey = appkey;
	}

	/**
	 * @return the secret
	 */
	public String getSecret()
	{
		return secret;
	}

	/**
	 * @param secret
	 *           the secret to set
	 */
	public void setSecret(final String secret)
	{
		this.secret = secret;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken()
	{
		return accessToken;
	}

	/**
	 * @param accessToken
	 *           the accessToken to set
	 */
	public void setAccessToken(final String accessToken)
	{
		this.accessToken = accessToken;
	}

	/**
	 * @return the expireTime
	 */
	public Long getExpireTime()
	{
		return expireTime;
	}

	/**
	 * @param expireTime
	 *           the expireTime to set
	 */
	public void setExpireTime(final Long expireTime)
	{
		this.expireTime = expireTime;
	}

	/**
	 * @return the expiresIn
	 */
	public Long getExpiresIn()
	{
		return expiresIn;
	}

	/**
	 * @param expiresIn
	 *           the expiresIn to set
	 */
	public void setExpiresIn(final Long expiresIn)
	{
		this.expiresIn = expiresIn;
	}

	/**
	 * @return the r1ExpiresIn
	 */
	public Long getR1ExpiresIn()
	{
		return r1ExpiresIn;
	}

	/**
	 * @param r1ExpiresIn
	 *           the r1ExpiresIn to set
	 */
	public void setR1ExpiresIn(final Long r1ExpiresIn)
	{
		this.r1ExpiresIn = r1ExpiresIn;
	}

	/**
	 * @return the r1Valid
	 */
	public Long getR1Valid()
	{
		return r1Valid;
	}

	/**
	 * @param r1Valid
	 *           the r1Valid to set
	 */
	public void setR1Valid(final Long r1Valid)
	{
		this.r1Valid = r1Valid;
	}

	/**
	 * @return the r2ExpiresIn
	 */
	public Long getR2ExpiresIn()
	{
		return r2ExpiresIn;
	}

	/**
	 * @param r2ExpiresIn
	 *           the r2ExpiresIn to set
	 */
	public void setR2ExpiresIn(final Long r2ExpiresIn)
	{
		this.r2ExpiresIn = r2ExpiresIn;
	}

	/**
	 * @return the r2Valid
	 */
	public Long getR2Valid()
	{
		return r2Valid;
	}

	/**
	 * @param r2Valid
	 *           the r2Valid to set
	 */
	public void setR2Valid(final Long r2Valid)
	{
		this.r2Valid = r2Valid;
	}

	/**
	 * @return the reExpiresIn
	 */
	public Long getReExpiresIn()
	{
		return reExpiresIn;
	}

	/**
	 * @param reExpiresIn
	 *           the reExpiresIn to set
	 */
	public void setReExpiresIn(final Long reExpiresIn)
	{
		this.reExpiresIn = reExpiresIn;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken()
	{
		return refreshToken;
	}

	/**
	 * @param refreshToken
	 *           the refreshToken to set
	 */
	public void setRefreshToken(final String refreshToken)
	{
		this.refreshToken = refreshToken;
	}

	/**
	 * @return the refreshTokenValidTime
	 */
	public Long getRefreshTokenValidTime()
	{
		return refreshTokenValidTime;
	}

	/**
	 * @param refreshTokenValidTime
	 *           the refreshTokenValidTime to set
	 */
	public void setRefreshTokenValidTime(final Long refreshTokenValidTime)
	{
		this.refreshTokenValidTime = refreshTokenValidTime;
	}

	/**
	 * @return the taobaoUserId
	 */
	public Long getTaobaoUserId()
	{
		return taobaoUserId;
	}

	/**
	 * @param taobaoUserId
	 *           the taobaoUserId to set
	 */
	public void setTaobaoUserId(final Long taobaoUserId)
	{
		this.taobaoUserId = taobaoUserId;
	}

	/**
	 * @return the taobaoUserNick
	 */
	public String getTaobaoUserNick()
	{
		return taobaoUserNick;
	}

	/**
	 * @param taobaoUserNick
	 *           the taobaoUserNick to set
	 */
	public void setTaobaoUserNick(final String taobaoUserNick)
	{
		this.taobaoUserNick = taobaoUserNick;
	}

	/**
	 * @return the tokenType
	 */
	public String getTokenType()
	{
		return tokenType;
	}

	/**
	 * @param tokenType
	 *           the tokenType to set
	 */
	public void setTokenType(final String tokenType)
	{
		this.tokenType = tokenType;
	}

	/**
	 * @return the w1ExpiresIn
	 */
	public Long getW1ExpiresIn()
	{
		return w1ExpiresIn;
	}

	/**
	 * @param w1ExpiresIn
	 *           the w1ExpiresIn to set
	 */
	public void setW1ExpiresIn(final Long w1ExpiresIn)
	{
		this.w1ExpiresIn = w1ExpiresIn;
	}

	/**
	 * @return the w1Valid
	 */
	public Long getW1Valid()
	{
		return w1Valid;
	}

	/**
	 * @param w1Valid
	 *           the w1Valid to set
	 */
	public void setW1Valid(final Long w1Valid)
	{
		this.w1Valid = w1Valid;
	}

	/**
	 * @return the w2ExpiresIn
	 */
	public Long getW2ExpiresIn()
	{
		return w2ExpiresIn;
	}

	/**
	 * @param w2ExpiresIn
	 *           the w2ExpiresIn to set
	 */
	public void setW2ExpiresIn(final Long w2ExpiresIn)
	{
		this.w2ExpiresIn = w2ExpiresIn;
	}

	/**
	 * @return the w2Valid
	 */
	public Long getW2Valid()
	{
		return w2Valid;
	}

	/**
	 * @param w2Valid
	 *           the w2Valid to set
	 */
	public void setW2Valid(final Long w2Valid)
	{
		this.w2Valid = w2Valid;
	}

	/**
	 * @return the authorized
	 */
	public String getAuthorized()
	{
		return authorized;
	}

	/**
	 * @param authorized
	 *           the authorized to set
	 */
	public void setAuthorized(final String authorized)
	{
		this.authorized = authorized;
	}


}
