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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.model.AccessToken;
import com.hybris.integration.service.datahub.DataHubRefundService;
import com.hybris.integration.service.datahub.bean.DatahubRefundListRequest;
import com.hybris.integration.service.tmall.AccessTokenService;
import com.hybris.integration.util.CommonUtils;


@Service("datahub.refundServiceImpl")
public class DataHubRefundServiceImpl extends BaseServiceImpl implements DataHubRefundService
{
	@Value("${datahub.refunds.api}")
	private String datahubRefundsAPI;

	@Autowired
	private AccessTokenService accessTokenService;

	@Override
	public Boolean saveRefunds(DatahubRefundListRequest dhRefundRequest, String integrationId) throws TmallAppException
	{
		JsonObject responseResults = null;
		AccessToken token = accessTokenService.get(integrationId);

		dhRefundRequest.setMarketplaceStoreId(token.getMarketplaceStoreId());
		dhRefundRequest.setSuffix(tmallStoreCustomSuffix);

		String url = datahubServerUrl + datahubRefundsAPI;
		final String body = CommonUtils.getGsonByBuilder(false).toJson(dhRefundRequest);
		responseResults = restTemplateUtil.post(url, body);
		String status = null;

		if (responseResults != null)
		{
			status = responseResults.get("code").getAsString();
		}
		return "0".equals(status) ? true : false;
	}

}
