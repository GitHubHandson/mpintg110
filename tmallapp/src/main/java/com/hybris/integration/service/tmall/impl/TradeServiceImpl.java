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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.tmall.APIService;
import com.hybris.integration.service.tmall.TradeService;
import com.hybris.integration.util.ResponseCode;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.TradeFullinfoGetRequest;
import com.taobao.api.request.TradesSoldGetRequest;
import com.taobao.api.response.TradeFullinfoGetResponse;
import com.taobao.api.response.TradesSoldGetResponse;


/**
 * TradeServiceImpl
 */
@Service("tmall.tradeServiceImpl")
public class TradeServiceImpl extends BaseServiceImpl implements TradeService
{
	private static Logger LOGGER = LoggerFactory.getLogger(AccessTokenServiceImpl.class);
	@Autowired
	private APIService apiService;

	public TradesSoldGetResponse getSoldTrades(String integrationId, TradesSoldGetRequest request) throws ApiException,
			TmallAppException
	{
		request.setUseHasNext(true);
		TradesSoldGetResponse response = getClient(integrationId).execute(request, getToken(integrationId));
		return response;
	}

	public Trade getTradeFullInfoByTid(String integrationId, long tid) throws ApiException, TmallAppException
	{
		TradeFullinfoGetRequest request = new TradeFullinfoGetRequest();
		String fieldsParam = apiService.getTmallDomainFieldByClassType(Trade.class, null) + ","
				+ apiService.getTmallDomainFieldByClassType(Order.class, "orders");

		request.setFields(fieldsParam);
		request.setTid(tid);
		Trade trade = null;

		TradeFullinfoGetResponse response = getClient(integrationId).execute(request, getToken(integrationId));

		if (response != null && StringUtils.isEmpty(response.getErrorCode()))
		{
			trade = response.getTrade();
		}
		else
		{
			response = getClient(integrationId).execute(request, getToken(integrationId));
			if (response != null && StringUtils.isEmpty(response.getErrorCode()))
			{
				trade = response.getTrade();
			}
			else
			{
				LOGGER.error("Tmall API taobao.trade.fullinfo.get failed request:" + response.getBody());
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
			}
		}
		return trade;
	}
}
