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
public class RefundRefuseRequest implements Serializable
{
	private static final long serialVersionUID = -3382402476073595053L;

	private String integrationId;

	private Long refundId;

	private String refundPhase;

	private Long refundVersion;

	private String refuseMessage;

	private byte[] refuseProof;

	private Long refuseReasonId;


	/**
	 * @return the refuseReasonId
	 */
	public Long getRefuseReasonId()
	{
		return refuseReasonId;
	}

	/**
	 * @param refuseReasonId
	 *           the refuseReasonId to set
	 */
	public void setRefuseReasonId(Long refuseReasonId)
	{
		this.refuseReasonId = refuseReasonId;
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
	 * @return the refundPhase
	 */
	public String getRefundPhase()
	{
		return refundPhase;
	}

	/**
	 * @param refundPhase
	 *           the refundPhase to set
	 */
	public void setRefundPhase(String refundPhase)
	{
		this.refundPhase = refundPhase;
	}

	/**
	 * @return the refundVersion
	 */
	public Long getRefundVersion()
	{
		return refundVersion;
	}

	/**
	 * @param refundVersion
	 *           the refundVersion to set
	 */
	public void setRefundVersion(Long refundVersion)
	{
		this.refundVersion = refundVersion;
	}

	/**
	 * @return the refuseMessage
	 */
	public String getRefuseMessage()
	{
		return refuseMessage;
	}

	/**
	 * @param refuseMessage
	 *           the refuseMessage to set
	 */
	public void setRefuseMessage(String refuseMessage)
	{
		this.refuseMessage = refuseMessage;
	}

	/**
	 * @return the refuseProof
	 */
	public byte[] getRefuseProof()
	{
		return refuseProof;
	}

	/**
	 * @param refuseProof
	 *           the refuseProof to set
	 */
	public void setRefuseProof(byte[] refuseProof)
	{
		this.refuseProof = refuseProof;
	}



}
