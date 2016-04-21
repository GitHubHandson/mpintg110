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
package com.hybris.integration.batch.trade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.tmall.TradeService;
import com.hybris.integration.service.tmall.impl.AccessTokenServiceImpl;
import com.hybris.integration.util.ResponseCode;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.TradesSoldGetRequest;
import com.taobao.api.response.TradesSoldGetResponse;


/**
 * Trade Reader
 */
@Component("tmall.tradeReader")
@Scope(value = "step")
public class TradeReader extends ListItemReader<Long>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenServiceImpl.class);

	/**
	 * 
	 * @param startCreated
	 * @param endCreated
	 * @param fileds
	 * @param status
	 * @param integrationId
	 * @param tradeService
	 * @throws Exception
	 */
	@Autowired
	public TradeReader(@Value("#{jobParameters['startCreated']}") final Date startCreated,
			@Value("#{jobParameters['endCreated']}") final Date endCreated,
			@Value("#{jobParameters['fields']}") final String fileds, @Value("#{jobParameters['status']}") final String status,
			@Value("#{jobParameters['integrationId']}") final String integrationId, final TradeService tradeService)
			throws Exception
	{
		super(readTradeTids(tradeService, integrationId, fileds, status, startCreated, endCreated));
	}

	/**
	 * 
	 * @param tradeService
	 * @param integrationId
	 * @param fields
	 * @param status
	 * @param startCreated
	 * @param endCreated
	 * @return
	 * @throws TmallAppException
	 * @throws ApiException
	 */
	public static List<Long> readTradeTids(final TradeService tradeService, final String integrationId, final String fields,
			final String status, final Date startCreated, final Date endCreated) throws TmallAppException, ApiException
	{

		LOGGER.info("Job Parameters[integrationId:" + integrationId + ",fields:" + fields + ",status:" + status + ",startCreated :"
				+ startCreated + ", endCreated : " + endCreated);

		final TradesSoldGetRequest req = new TradesSoldGetRequest();

		if (startCreated != null && endCreated != null)
		{
			req.setStartCreated(startCreated);
			req.setEndCreated(endCreated);
		}
		req.setFields(fields);
		req.setStatus(status);

		TradesSoldGetResponse response = new TradesSoldGetResponse();

		long pageNo = 1;

		final List<Long> tids = new ArrayList<Long>();
		do
		{
			req.setPageNo(pageNo);

			response = tradeService.getSoldTrades(integrationId, req);

			if (StringUtils.isNotEmpty(response.getErrorCode()))
			{
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), "Response code from tmall = "
						+ response.getSubCode() + "; Response msg from tmall" + response.getSubMsg());
			}
			final List<Trade> trades = response.getTrades();

			if (response.getHasNext() == false && trades == null)
			{
				break;
			}

			for (final Trade trade : trades)
			{
				tids.add(trade.getTid());
			}

			pageNo++;
		}
		while (response.getHasNext());
		LOGGER.info("Query to the data size :" + tids.size());
		return tids;
	}

	/**
	 * Handle error
	 */
	@OnReadError
	public void onError()
	{
	}
}
