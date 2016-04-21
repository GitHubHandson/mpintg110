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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.datahub.DataHubTradeService;
import com.hybris.integration.service.datahub.bean.DatahubTradeRequest;
import com.hybris.integration.service.tmall.AccessTokenService;
import com.hybris.integration.util.ResponseCode;
import com.taobao.api.domain.Trade;


/**
 * Trade Writer
 */
@Component("tmall.tradeWriter")
@Scope(value = "step")
public class TradeWriter implements ItemWriter<Trade>, ItemWriteListener<Trade>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeWriter.class);

	@Value("#{jobParameters['integrationId']}")
	private String integrationId;

	@Value("#{jobParameters['productCatalogVersion']}")
	private String productCatalogVersion;

	@Value("#{jobParameters['currency']}")
	private String currency;

	@Autowired
	private AccessTokenService accessTokenService;

	@Autowired
	private DataHubTradeService dhTradeService;

	@Override
	public void write(final List<? extends Trade> trades) throws Exception
	{

		final DatahubTradeRequest tradeRequest = new DatahubTradeRequest();

		tradeRequest.setCurrency(currency);
		tradeRequest.setProductCatalogVersion(productCatalogVersion);
		tradeRequest.setTrades(trades);

		if (!dhTradeService.saveOrders(tradeRequest, integrationId))
		{
			throw new TmallAppException(ResponseCode.REQUEST_DATAHUB_ERROR.getCode(), ResponseCode.REQUEST_DATAHUB_ERROR.getValue());
		}

	}

	@Override
	public void beforeWrite(final List<? extends Trade> items)
	{

	}

	@Override
	public void afterWrite(final List<? extends Trade> items)
	{
		LOGGER.info("Post to datahub :" + items.size());
	}

	@Override
	public void onWriteError(final Exception exception, final List<? extends Trade> items)
	{
		LOGGER.error("Failed to send the current order list:" + items.size());
	}


}
