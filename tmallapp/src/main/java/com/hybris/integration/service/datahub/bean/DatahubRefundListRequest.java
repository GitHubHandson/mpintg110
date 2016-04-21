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
 * Datahub Refund Request
 */
public class DatahubRefundListRequest implements Serializable
{

	private static final long serialVersionUID = -8633267265884789029L;

	private String marketplaceStoreId;

	private String suffix;

	private List<?> refunds;

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
	public void setMarketplaceStoreId(String marketplaceStoreId)
	{
		this.marketplaceStoreId = marketplaceStoreId;
	}

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
	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	/**
	 * @return the refunds
	 */
	public List<?> getRefunds()
	{
		return refunds;
	}

	/**
	 * @param refunds
	 *           the refunds to set
	 */
	public void setRefunds(List<?> refunds)
	{
		this.refunds = refunds;
	}


}
