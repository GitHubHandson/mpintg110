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
package com.hybris.datahub.service.impl.handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.datahub.service.JsonService;
import com.hybris.datahub.service.MarketplaceIntegrationHandler;
import com.hybris.datahub.service.MarketplaceIntegrationService;

public class DefaultTmallRefundTradesHandler implements MarketplaceIntegrationHandler
{

	private String rawItemType;
	private MarketplaceIntegrationService marketplaceIntegrationService;
	private JsonService jsonService;
	private Map<String, String> refundTypes;
	private Map<String, String> refundStatuses;
	private String refundTypeDefault;
	private String refundStatusDefault;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultTmallRefundTradesHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hybris.datahub.service.MarketplaceIntegrationHandler#handle(java.lang.String)
	 */
	@Override
	public int handle(String context)
	{
		LOG.debug("Tmall Refund Trades: " + context);

		final String json = context;
		final List<Map<String, String>> result = jsonService.parse(json);
		
		final String marketplaceStoreId = result.get(0).get("marketplaceStoreId");
		final String suffix = result.get(0).get("suffix");
		final String refundsJson = result.get(0).get("refunds");
		final List<Map<String, String>> refunds = jsonService.parse(refundsJson);
		
		for (final Map<String, String> refund : refunds)
		{
			refund.put("marketplaceStoreId", marketplaceStoreId);
			refund.put("refundId", refund.get("refundId"));
			refund.put("refundFee", refund.get("refundFee"));
			refund.put("totalFee", refund.get("totalFee"));
			refund.put("refundReason", refund.get("reason"));
			refund.put("refundCreatedDate", refund.get("created"));
			refund.put("refundStatus", refund.get("status"));
			refund.put("oid", refund.get("oid"));	
			refund.put("tid", refund.get("tid"));	
			
			String buyerNick = refund.get("buyerNick");
			buyerNick = StringUtils.isBlank(suffix) ? buyerNick : buyerNick + "(" + suffix + ")";
			refund.put("buyerNick", buyerNick);
			
			refund.put("refundAction", extractRefundStatus(refund.get("status")));
			refund.put("refundType", extractRefundType(refund.get("hasGoodReturn")));
			
			marketplaceIntegrationService.processRawItem(rawItemType, refund);
		}		
		return 1;
	}

	private String extractRefundStatus(final String orderRefundStatus) {
		if (refundStatuses.containsKey(orderRefundStatus))
		{
			return refundStatuses.get(orderRefundStatus);
		}
		else
		{
			return refundStatuses.get(refundStatusDefault);
		}
	}

	private String extractRefundType(final String orderRefundType)
	{
		if (refundTypes.containsKey(orderRefundType))
		{
			return refundTypes.get(orderRefundType);
		}
		else
		{
			return refundTypes.get(refundTypeDefault);
		}
	}
	
	public Map<String, String> getRefundStatuses() {
		return refundStatuses;
	}

	public void setRefundStatuses(Map<String, String> refundStatuses) {
		this.refundStatuses = refundStatuses;
	}

	public String getRefundStatusDefault() {
		return refundStatusDefault;
	}

	public void setRefundStatusDefault(String refundStatusDefault) {
		this.refundStatusDefault = refundStatusDefault;
	}
	
	public String getRefundTypeDefault() {
		return refundTypeDefault;
	}

	public void setRefundTypeDefault(String refundTypeDefault) {
		this.refundTypeDefault = refundTypeDefault;
	}

	public Map<String, String> getRefundTypes() {
		return refundTypes;
	}

	public void setRefundTypes(Map<String, String> refundTypes) {
		this.refundTypes = refundTypes;
	}

	public void setMarketplaceIntegrationService(MarketplaceIntegrationService marketplaceIntegrationService)
	{
		this.marketplaceIntegrationService = marketplaceIntegrationService;
	}

	public void setJsonService(JsonService jsonService)
	{
		this.jsonService = jsonService;
	}

	public void setRawItemType(String rawItemType)
	{
		this.rawItemType = rawItemType;
	}
}
