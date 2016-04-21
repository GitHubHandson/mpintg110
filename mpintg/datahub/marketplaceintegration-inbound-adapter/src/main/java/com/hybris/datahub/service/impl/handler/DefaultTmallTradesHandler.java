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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.datahub.service.JsonService;
import com.hybris.datahub.service.MarketplaceIntegrationHandler;
import com.hybris.datahub.service.MarketplaceIntegrationService;


/**
 * DefaultTmallTradesHandler
 */
public class DefaultTmallTradesHandler implements MarketplaceIntegrationHandler
{
	private static final String RAW_ITEM_TMALL_TRADE = "tmallTrade";
	private static final String RAW_ITEM_TMALL_ORDER = "tmallOrder";

	private static final Logger LOG = LoggerFactory.getLogger(DefaultTmallTradesHandler.class);

	private Map<String, String> rawItemTypes;
	private Map<String, String> orderCountries;
	private Map<String, String> orderStatusesToPaymentStatuses;
	private String orderStatusDefault;
	private String orderCountryDefault;
	private Map<String, String> defaultValues;
	private JsonService jsonService;
	private MarketplaceIntegrationService marketplaceIntegrationService;

	@Override
	public int handle(final String context)
	{
		final String json = context;
		final List<Map<String, String>> result = jsonService.parse(json);
		final String marketplaceStoreId = result.get(0).get("marketplaceStoreId");
		final String suffix = result.get(0).get("suffix");
		final String currency = result.get(0).get("currency");
		final String productCatalogVersion = result.get(0).get("productCatalogVersion");
		final String tradesJson = result.get(0).get("trades");
		final List<Map<String, String>> trades = jsonService.parse(tradesJson);
		for (final Map<String, String> trade : trades)
		{
			escapeAddress(trade);

			final String orderEntriesJson = trade.get("orders");
			final String tradeId = trade.get("tid");
			final String tradeCreatedDate = trade.get("created");
			final String tradePaymentTransactionUUID = trade.get("tid");
			final String tradeAlipayNo = trade.get("alipayNo");
			trade.remove("orders");

			String buyerNick = trade.get("buyerNick");
			buyerNick = StringUtils.isBlank(suffix) ? buyerNick : buyerNick + "(" + suffix + ")";
			trade.put("buyerNick", buyerNick);

			trade.put("marketplaceStoreId", marketplaceStoreId);
			trade.put("userId", trade.get("buyerNick"));
			trade.put("currency", currency);
			trade.put("tmallOrderStatus", trade.get("status"));
			trade.put("userGuid", trade.get("buyerNick"));
			trade.put("latestTradeStatus", trade.get("status"));
			trade.put("latestTransactionType", defaultValues.get("latestTransactionType"));
			trade.put("paymentProvider", defaultValues.get("paymentProvider"));
			trade.put("loginDisabled", defaultValues.get("loginDisabled"));
			trade.put("paymentTransactionId", tradePaymentTransactionUUID);
			trade.put("orderCountry", extractOrderCountry(trade.get("receiverCountry")));
			trade.put("baseSite", "electronics-cn");

			List<Map<String, String>> orderEntries = jsonService.parse(orderEntriesJson);
			trade.put("tmallOrderEntriesNo", String.valueOf(orderEntries.size()));


			putCustomerAddress(trade);
			putDeliveryAddress(trade);
			putPaymentDetail(trade);
			marketplaceIntegrationService.processRawItem(rawItemTypes.get(RAW_ITEM_TMALL_TRADE), trade);

			List<Map<String, String>> adjustedOrderEntries = new ArrayList<Map<String, String>>();
			for (final Map<String, String> orderEntry : orderEntries)
			{
				try
				{
					final String orderEntryId = orderEntry.get("oid");
					orderEntry.put("paymentTransactionEntryId", orderEntryId);
					orderEntry.put("product", String.format("%s:%s", extractProductId(orderEntry), productCatalogVersion));
					orderEntry.put("orderEntryId", orderEntryId);
					orderEntry.put("order", tradeId);
					orderEntry.put("promotionMatcher", defaultValues.get("promotionMatcher"));
					orderEntry.put("unit", defaultValues.get("unit"));
					orderEntry.put("paymentTransaction", tradePaymentTransactionUUID);
					orderEntry.put("time", tradeCreatedDate);
					orderEntry.put("transactionStatus", defaultValues.get("transactionStatus"));
					orderEntry.put("transactionStatusDetails",
							String.format(defaultValues.get("transactionStatusDetails"), orderEntry.get("status")));
					orderEntry.put("alipayNo", tradeAlipayNo);
					orderEntry.put("tid", tradeId);
					orderEntry.put("tradeStatus", orderEntry.get("status"));
					orderEntry.put("notifyType", defaultValues.get("notifyType"));
					orderEntry.put("notifyId", UUID.randomUUID().toString());
					orderEntry.put("discountValuesInternal", extractDiscount(orderEntry));
					orderEntry.put("orderEntryStatus", orderEntry.get("status"));
					orderEntry.put("refundStatus", orderEntry.get("refundStatus"));
					adjustedOrderEntries.add(orderEntry);
				}
				catch (Exception e)
				{
					LOG.error("Convert order entry encounter error,orderEntry:" + orderEntry, e);
				}
			}
			marketplaceIntegrationService.processRawItem(rawItemTypes.get(RAW_ITEM_TMALL_ORDER), adjustedOrderEntries);
		}
		return trades.size();
	}

	private void escapeAddress(final Map<String, String> trade)
	{
		trade.put("receiverAddress", trade.get("receiverAddress").replaceAll(":", "："));
	}

	private void putCustomerAddress(final Map<String, String> trade)
	{
		trade.put("billingAddress", defaultValues.get("billingAddress"));
		trade.put("shippingAddress", defaultValues.get("shippingAddress"));
		trade.put("cityDistrict", String.format("%s:%s", trade.get("receiverCity"), trade.get("receiverDistrict")));
	}

	private void putPaymentDetail(final Map<String, String> trade)
	{
		final String tradeStatus = trade.get("status");
		if (orderStatusesToPaymentStatuses.containsKey(tradeStatus))
		{
			trade.put("paymentStatus", orderStatusesToPaymentStatuses.get(tradeStatus));
			if (defaultValues.get("paymentStatusPaid").equals(orderStatusesToPaymentStatuses.get(tradeStatus)))
			{
				trade.put("paymentMode", defaultValues.get("paymentMode"));
			}
		}
		else
		{
			trade.put("paymentStatus", "");
		}
		trade.put("paymentInfo", trade.get("tid"));
	}

	private void putDeliveryAddress(final Map<String, String> trade)
	{
		//customer id in hybris is lowercase
		final String deliveryAddress = String.format("%s:%s", trade.get("buyerNick").toLowerCase(), trade.get("receiverAddress"));
		trade.put("deliveryAddress", deliveryAddress);
	}

	private String extractProductId(final Map<String, String> orderEntry)
	{
		final String outerSkuId = orderEntry.get("outerSkuId");
		final String outerIid = orderEntry.get("outerIid");

		if (StringUtils.isBlank(outerSkuId) && StringUtils.isBlank(outerIid))
		{
			LOG.error("Miss product outerSkuId and outerIid. Order entry id = " + orderEntry.get("oid"));
			throw new IllegalArgumentException("product id is null for order entry "+orderEntry);
		}
		return StringUtils.isBlank(outerSkuId) ? outerIid : outerSkuId;
	}

	private String extractDiscount(final Map<String, String> orderEntry)
	{
		double discount = 0.0;
		try
		{
			discount = Double.parseDouble(orderEntry.get("adjustFee")) + Double.parseDouble(orderEntry.get("discountFee"));
		}
		catch (final NumberFormatException e)
		{
			LOG.error(e.getMessage(), e);
		}
		return String.valueOf(discount);
	}

	private String extractOrderCountry(final String tmallOrderCountry)
	{
		if (getOrderCountries().containsKey(tmallOrderCountry))
		{
			return getOrderCountries().get(tmallOrderCountry);
		}
		else
		{
			return orderCountryDefault;
		}
	}

	/**
	 * @param jsonService
	 */
	public void setJsonService(final JsonService jsonService)
	{
		this.jsonService = jsonService;
	}

	/**
	 * @param marketplaceIntegrationService
	 *           the marketplaceIntegrationService to set
	 */
	public void setMarketplaceIntegrationService(final MarketplaceIntegrationService marketplaceIntegrationService)
	{
		this.marketplaceIntegrationService = marketplaceIntegrationService;
	}

	/**
	 * @param rawItemTypes
	 *           the rawItemTypes to set
	 */
	public void setRawItemTypes(final Map<String, String> rawItemTypes)
	{
		this.rawItemTypes = rawItemTypes;
	}

	/**
	 * @return the orderStatusDefault
	 */
	public String getOrderStatusDefault()
	{
		return orderStatusDefault;
	}

	/**
	 * @return the orderStatusDefault
	 */
	public String getOrderCountryDefault()
	{
		return orderCountryDefault;
	}

	/**
	 * @param orderStatusDefault
	 *           the orderStatusDefault to set
	 */
	public void setOrderStatusDefault(final String orderStatusDefault)
	{
		this.orderStatusDefault = orderStatusDefault;
	}

	/**
	 * @param orderStatusDefault
	 *           the orderStatusDefault to set
	 */
	public void setOrderCountryDefault(final String orderCountryDefault)
	{
		this.orderCountryDefault = orderCountryDefault;
	}

	/**
	 * @return the orderStatusesToPaymentStatuses
	 */
	public Map<String, String> getOrderStatusesToPaymentStatuses()
	{
		return orderStatusesToPaymentStatuses;
	}

	/**
	 * @param orderStatusesToPaymentStatuses
	 *           the orderStatusesToPaymentStatuses to set
	 */
	public void setOrderStatusesToPaymentStatuses(final Map<String, String> orderStatusesToPaymentStatuses)
	{
		this.orderStatusesToPaymentStatuses = orderStatusesToPaymentStatuses;
	}

	/**
	 * @return the defaultValues
	 */
	public Map<String, String> getDefaultValues()
	{
		return defaultValues;
	}

	/**
	 * @param defaultValues
	 *           the defaultValues to set
	 */
	public void setDefaultValues(final Map<String, String> defaultValues)
	{
		this.defaultValues = defaultValues;
	}

	public Map<String, String> getOrderCountries()
	{
		return orderCountries;
	}

	public void setOrderCountries(Map<String, String> orderCountries)
	{
		this.orderCountries = orderCountries;
	}

}
