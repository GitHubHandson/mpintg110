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
package com.hybris.integration.vo.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Trade Criteria
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeDownloadRequest implements Serializable
{
	private static final long serialVersionUID = 176995946265473550L;

	private String integrationId;
	private Long batchSize = 100l;
	private String startCreated;
	private String endCreated;
	private String status;
	private String currency;
	private String productCatalogVersion;

	/**
	 * @return the integrationId
	 */
	public String getIntegrationId()
	{
		return integrationId;
	}

	/**
	 * @param integrationId
	 *           the integrationId to set
	 */
	public void setIntegrationId(final String integrationId)
	{
		this.integrationId = integrationId;
	}

	/**
	 * @return the batchSize
	 */
	public Long getBatchSize()
	{
		return batchSize;
	}

	/**
	 * @param batchSize
	 *           the batchSize to set
	 */
	public void setBatchSize(final Long batchSize)
	{
		this.batchSize = batchSize;
	}

	/**
	 * @return the startCreated
	 */
	public String getStartCreated()
	{
		return startCreated;
	}

	/**
	 * @param startCreated
	 *           the startCreated to set
	 */
	public void setStartCreated(final String startCreated)
	{
		this.startCreated = startCreated;
	}

	/**
	 * @return the endCreated
	 */
	public String getEndCreated()
	{
		return endCreated;
	}

	/**
	 * @param endCreated
	 *           the endCreated to set
	 */
	public void setEndCreated(final String endCreated)
	{
		this.endCreated = endCreated;
	}

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus(final String status)
	{
		this.status = status;
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


}
