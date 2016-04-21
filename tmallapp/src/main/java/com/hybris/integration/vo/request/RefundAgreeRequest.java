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
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RefundAgreeRequest implements Serializable
{
	private static final long serialVersionUID = 7059840380850970475L;

	private String code;
	private String integrationId;
	private Long refundId;
	private BigDecimal amount;
	private Long version;
	private String phase;

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

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
	 * @return the refundId
	 */
	public Long getRefundId()
	{
		return refundId;
	}

	/**
	 * @param refundId
	 *           the refundId to set
	 */
	public void setRefundId(Long refundId)
	{
		this.refundId = refundId;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount()
	{
		return amount;
	}

	/**
	 * @param amount
	 *           the amount to set
	 */
	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	/**
	 * @return the version
	 */
	public Long getVersion()
	{
		return version;
	}

	/**
	 * @param version
	 *           the version to set
	 */
	public void setVersion(Long version)
	{
		this.version = version;
	}

	/**
	 * @return the phase
	 */
	public String getPhase()
	{
		return phase;
	}

	/**
	 * @param phase
	 *           the phase to set
	 */
	public void setPhase(String phase)
	{
		this.phase = phase;
	}



}
