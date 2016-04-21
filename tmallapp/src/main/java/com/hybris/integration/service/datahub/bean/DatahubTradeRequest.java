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
package com.hybris.integration.service.datahub.bean;

import java.io.Serializable;
import java.util.List;


/**
 * Datahub OrderRequest
 */
public class DatahubTradeRequest implements Serializable
{
	private static final long serialVersionUID = -1589830205687792638L;

	private String marketplaceStoreId;
	private String productCatalogVersion;
	private String currency;
	private String suffix;
	private List<?> trades;

	/**
	 * @return the suffix
	 */
	public String getSuffix()
	{
		return suffix;
	}

	/**
	 * @param suffix
	 *           the suffix to set
	 */
	public void setSuffix(final String suffix)
	{
		this.suffix = suffix;
	}


	/**
	 * @return the marketplaceStoreId
	 */
	public String getMarketplaceStoreId()
	{
		return marketplaceStoreId;
	}

	/**
	 * @param marketplaceStoreId
	 *           the marketplaceStoreId to set
	 */
	public void setMarketplaceStoreId(final String marketplaceStoreId)
	{
		this.marketplaceStoreId = marketplaceStoreId;
	}

	/**
	 * @return the productCatalogVersion
	 */
	public String getProductCatalogVersion()
	{
		return productCatalogVersion;
	}

	/**
	 * @param productCatalogVersion
	 *           the productCatalogVersion to set
	 */
	public void setProductCatalogVersion(final String productCatalogVersion)
	{
		this.productCatalogVersion = productCatalogVersion;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency()
	{
		return currency;
	}

	/**
	 * @param currency
	 *           the currency to set
	 */
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	/**
	 * @return the trades
	 */
	public List<?> getTrades()
	{
		return trades;
	}

	/**
	 * @param trades
	 *           the trades to set
	 */
	public void setTrades(final List<?> trades)
	{
		this.trades = trades;
	}


}
