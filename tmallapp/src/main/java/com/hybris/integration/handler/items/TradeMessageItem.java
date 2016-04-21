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
package com.hybris.integration.handler.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hybris.integration.handler.invocation.MessageTopic;
import com.hybris.integration.handler.invocation.Request;
import com.hybris.integration.service.datahub.DataHubTradeService;
import com.hybris.integration.service.datahub.bean.DatahubTradeRequest;
import com.hybris.integration.service.tmall.AccessTokenService;
import com.hybris.integration.service.tmall.TradeService;
import com.hybris.integration.vo.request.TmcMessageRequest;
import com.taobao.api.domain.Trade;


/**
 * Trade Message Item
 */
@Component
public class TradeMessageItem extends MessageItem
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TradeMessageItem.class);
	@Autowired
	private TradeService tradeService;

	@Autowired
	private AccessTokenService accessTokenService;


	@Autowired
	private DataHubTradeService dhTradeService;

	@Override
	public boolean excute(final Request request)
	{
		final String content = request.getContent();

		if (StringUtils.isEmpty(content))
		{
			return false;
		}

		try
		{
			final JsonElement jsonElement = new JsonParser().parse(content);
			final JsonObject jsonObject = jsonElement.getAsJsonObject();
			final Long tid = jsonObject.get("tid").getAsLong();

			final Trade trade = tradeService.getTradeFullInfoByTid(request.getIntegrationId(), tid);

			if (trade == null)
			{
				LOGGER.error("Orders can not be found[integrationId=" + request.getIntegrationId() + ",tid=" + tid + "].");
				return false;
			}

			final List<Trade> trades = new ArrayList<Trade>();
			trades.add(trade);

			final DatahubTradeRequest tradeRequest = new DatahubTradeRequest();

			if (request.getTmcMessageCriteria() != null)
			{
				final TmcMessageRequest criteria = request.getTmcMessageCriteria();

				tradeRequest.setCurrency(criteria.getCurrency());
				tradeRequest.setProductCatalogVersion(criteria.getProductCatalogVersion());

			}

			tradeRequest.setTrades(trades);

			return dhTradeService.saveOrders(tradeRequest, request.getIntegrationId());

		}
		catch (final Exception e)
		{
			return false;
		}
	}

	@Override
	public String getMessageTopic()
	{
		return MessageTopic.TAOBAO_TRADE.getCode();
	}

}
