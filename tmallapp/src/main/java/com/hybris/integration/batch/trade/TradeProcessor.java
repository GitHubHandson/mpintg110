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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hybris.integration.service.tmall.APIService;
import com.hybris.integration.service.tmall.TradeService;
import com.hybris.integration.service.tmall.impl.AccessTokenServiceImpl;
import com.taobao.api.domain.Trade;


/**
 * Trade Processor
 */
@Component("tmall.tradeProcessor")
@Scope(value = "step")
public class TradeProcessor implements ItemProcessor<Long, Trade>, ItemProcessListener<Long, Trade>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenServiceImpl.class);

	@Resource(name = "tmall.APIServiceImpl")
	private APIService apiService;

	@Resource(name = "tmall.tradeServiceImpl")
	private TradeService orderService;

	@Value("#{jobParameters['integrationId']}")
	private String integrationId;

	@Override
	public Trade process(final Long tid) throws Exception
	{

		final Trade trade = orderService.getTradeFullInfoByTid(integrationId, tid);

		return trade;
	}

	@Override
	public void onProcessError(final Long item, final Exception e)
	{
		LOGGER.error("An exception occurs in the processor, the item code is :" + item);
	}

	@Override
	public void beforeProcess(final Long item)
	{
	}

	@Override
	public void afterProcess(final Long item, final Trade result)
	{
	}

}
