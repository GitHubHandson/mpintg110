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
import com.hybris.integration.service.datahub.DataHubRefundService;
import com.hybris.integration.service.datahub.DataHubTradeService;
import com.hybris.integration.service.datahub.bean.DatahubRefundListRequest;
import com.hybris.integration.service.datahub.bean.DatahubTradeRequest;
import com.hybris.integration.service.tmall.AccessTokenService;
import com.hybris.integration.service.tmall.RefundService;
import com.hybris.integration.service.tmall.TradeService;
import com.hybris.integration.vo.request.TmcMessageRequest;
import com.taobao.api.domain.Refund;
import com.taobao.api.domain.Trade;


/**
 * Refund Message Item
 */
@Component
public class RefundMessageItem extends MessageItem
{
	private static Logger LOGGER = LoggerFactory.getLogger(RefundMessageItem.class);
	@Autowired
	private RefundService refundService;

	@Autowired
	private TradeService tradeService;

	@Autowired
	private AccessTokenService accessTokenService;

	@Autowired
	private DataHubRefundService dhRefundService;

	@Autowired
	private DataHubTradeService dhTradeService;

	@Override
	public boolean excute(Request request)
	{
		String content = request.getContent();

		if (StringUtils.isEmpty(content))
		{
			return false;
		}

		try
		{
			JsonElement jsonElement = new JsonParser().parse(content);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Long refundId = jsonObject.get("refund_id").getAsLong();

			Refund refund = refundService.refundGetDetails(request.getIntegrationId(), refundId);

			if (refund == null)
			{
				LOGGER.error("Refund can not be found[integrationId=" + request.getIntegrationId() + ",refundId=" + refundId + "].");
				return false;
			}

			List<Refund> refunds = new ArrayList<Refund>();
			refunds.add(refund);
			DatahubRefundListRequest refundListRequest = new DatahubRefundListRequest();

			refundListRequest.setRefunds(refunds);

			// Get the trades
			Trade trade = tradeService.getTradeFullInfoByTid(request.getIntegrationId(), refund.getTid());

			if (trade == null)
			{
				LOGGER.error("Orders can not be found[integrationId=" + request.getIntegrationId() + ",tid=" + refund.getTid() + "].");
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

			boolean tradeSendFlag = dhTradeService.saveOrders(tradeRequest, request.getIntegrationId());
			boolean refundSendFlag = dhRefundService.saveRefunds(refundListRequest, request.getIntegrationId());

			return refundSendFlag && tradeSendFlag;

		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public String getMessageTopic()
	{
		return MessageTopic.TAOBAO_REFUND.getCode();
	}

}
