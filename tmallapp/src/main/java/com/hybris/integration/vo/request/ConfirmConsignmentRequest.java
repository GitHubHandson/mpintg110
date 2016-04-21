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
 * Logistics Criteria
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfirmConsignmentRequest implements Serializable
{
	private static final long serialVersionUID = 2177775230610648592L;

	private Long senderId;
	private Long cancelId;
	private Long tid;
	private Long isSplit; //1 or 0

	private String subTid;
	private String outSid;
	private String feature;
	private String sellerIp;
	private String companyCode;
	private String marketplaceLogId;
	private String integrationId;
	private String exportItemCode;


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
	public void setIntegrationId(String integrationId)
	{
		this.integrationId = integrationId;
	}

	/**
	 * @return the subTid
	 */
	public String getSubTid()
	{
		return subTid;
	}

	/**
	 * @param subTid
	 *           the subTid to set
	 */
	public void setSubTid(String subTid)
	{
		this.subTid = subTid;
	}

	/**
	 * @return the tid
	 */
	public Long getTid()
	{
		return tid;
	}

	/**
	 * @param tid
	 *           the tid to set
	 */
	public void setTid(Long tid)
	{
		this.tid = tid;
	}

	/**
	 * @return the isSplit
	 */
	public Long getIsSplit()
	{
		return isSplit;
	}

	/**
	 * @param isSplit
	 *           the isSplit to set
	 */
	public void setIsSplit(Long isSplit)
	{
		this.isSplit = isSplit;
	}

	/**
	 * @return the outSid
	 */
	public String getOutSid()
	{
		return outSid;
	}

	/**
	 * @param outSid
	 *           the outSid to set
	 */
	public void setOutSid(String outSid)
	{
		this.outSid = outSid;
	}

	/**
	 * @return the companyCode
	 */
	public String getCompanyCode()
	{
		return companyCode;
	}

	/**
	 * @param companyCode
	 *           the companyCode to set
	 */
	public void setCompanyCode(String companyCode)
	{
		this.companyCode = companyCode;
	}

	/**
	 * @return the senderId
	 */
	public Long getSenderId()
	{
		return senderId;
	}

	/**
	 * @param senderId
	 *           the senderId to set
	 */
	public void setSenderId(Long senderId)
	{
		this.senderId = senderId;
	}

	/**
	 * @return the cancelId
	 */
	public Long getCancelId()
	{
		return cancelId;
	}

	/**
	 * @param cancelId
	 *           the cancelId to set
	 */
	public void setCancelId(Long cancelId)
	{
		this.cancelId = cancelId;
	}

	/**
	 * @return the feature
	 */
	public String getFeature()
	{
		return feature;
	}

	/**
	 * @param feature
	 *           the feature to set
	 */
	public void setFeature(String feature)
	{
		this.feature = feature;
	}

	/**
	 * @return the sellerIp
	 */
	public String getSellerIp()
	{
		return sellerIp;
	}

	/**
	 * @param sellerIp
	 *           the sellerIp to set
	 */
	public void setSellerIp(String sellerIp)
	{
		this.sellerIp = sellerIp;
	}

	/**
	 * @return the marketplaceLogId
	 */
	public String getMarketplaceLogId()
	{
		return marketplaceLogId;
	}

	/**
	 * @param marketplaceLogId
	 *           the marketplaceLogId to set
	 */
	public void setMarketplaceLogId(String marketplaceLogId)
	{
		this.marketplaceLogId = marketplaceLogId;
	}

	/**
	 * @return the exportItemCode
	 */
	public String getExportItemCode()
	{
		return exportItemCode;
	}

	/**
	 * @param exportItemCode
	 *           the exportItemCode to set
	 */
	public void setExportItemCode(String exportItemCode)
	{
		this.exportItemCode = exportItemCode;
	}

}
