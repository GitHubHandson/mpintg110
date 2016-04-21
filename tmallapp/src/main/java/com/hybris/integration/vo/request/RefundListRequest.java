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


@JsonIgnoreProperties(ignoreUnknown = true)
public class RefundListRequest implements Serializable
{
	private static final long serialVersionUID = 4985903144698214984L;

	private Long batchSize = 100l;
	private String startCreated;
	private String endCreated;
	private String status;
	private String currency;
	private String productCatalogVersion;

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
	public void setBatchSize(Long batchSize)
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
	public void setStartCreated(String startCreated)
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
	public void setEndCreated(String endCreated)
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
	public void setStatus(String status)
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
	public void setCurrency(String currency)
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
	public void setProductCatalogVersion(String productCatalogVersion)
	{
		this.productCatalogVersion = productCatalogVersion;
	}

}
