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

import com.hybris.integration.exception.TmallAppException;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.TradesSoldGetRequest;
import com.taobao.api.response.TradesSoldGetResponse;


/**
 *
 */
public interface TradeService
{
	/**
	 * @param integrationId
	 * @param request
	 * @return response
	 * @throws ApiException
	 * @throws TmallAppException
	 */
	public TradesSoldGetResponse getSoldTrades(String integrationId, TradesSoldGetRequest request) throws ApiException,
			TmallAppException;

	/**
	 * @param integrationId
	 * @param tid
	 * @return Trade details
	 * @throws ApiException
	 * @throws TmallAppException
	 */
	public Trade getTradeFullInfoByTid(String integrationId, long tid) throws ApiException, TmallAppException;

}
