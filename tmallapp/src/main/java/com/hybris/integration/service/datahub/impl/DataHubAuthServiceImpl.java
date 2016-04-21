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
package com.hybris.integration.service.datahub.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.hybris.integration.model.AccessToken;
import com.hybris.integration.service.datahub.DataHubAuthService;
import com.hybris.integration.util.CommonUtils;


/**
 * Datahub AuthServiceImpl
 */
@Service("datahub.authServiceImpl")
public class DataHubAuthServiceImpl extends BaseServiceImpl implements DataHubAuthService
{

	@Value("${datahub.auth.api}")
	private String datahubAuthAPI;

	@Override
	public Boolean saveAuthInfo(final AccessToken accessToken)
	{
		JsonObject responseResults = null;

		String url = datahubServerUrl + datahubAuthAPI;
		accessToken.setSecret(null);
		final String data = CommonUtils.getGsonByBuilder().toJson(accessToken);

		responseResults = restTemplateUtil.post(url, data);
		String status = null;

		if (responseResults != null)
		{
			status = responseResults.get("code").getAsString();
		}
		return "0".equals(status) ? true : false;
	}
}
