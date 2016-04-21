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
public class ReturngoodsAgreeRequest implements Serializable
{

	private static final long serialVersionUID = -2106504890667426941L;

	private String integrationId;

	private long refundId;
	private String name;
	private String address;
	private String post;
	private String tel;
	private String mobile;
	private String remark;
	private String refundPhase;
	private long refundVersion;
	private long sellerAddressId;

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
	public long getRefundId()
	{
		return refundId;
	}

	/**
	 * @param refundId
	 *           the refundId to set
	 */
	public void setRefundId(long refundId)
	{
		this.refundId = refundId;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the address
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 *           the address to set
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}

	/**
	 * @return the post
	 */
	public String getPost()
	{
		return post;
	}

	/**
	 * @param post
	 *           the post to set
	 */
	public void setPost(String post)
	{
		this.post = post;
	}

	/**
	 * @return the tel
	 */
	public String getTel()
	{
		return tel;
	}

	/**
	 * @param tel
	 *           the tel to set
	 */
	public void setTel(String tel)
	{
		this.tel = tel;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile()
	{
		return mobile;
	}

	/**
	 * @param mobile
	 *           the mobile to set
	 */
	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	/**
	 * @return the remark
	 */
	public String getRemark()
	{
		return remark;
	}

	/**
	 * @param remark
	 *           the remark to set
	 */
	public void setRemark(String remark)
	{
		this.remark = remark;
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
	public long getRefundVersion()
	{
		return refundVersion;
	}

	/**
	 * @param refundVersion
	 *           the refundVersion to set
	 */
	public void setRefundVersion(long refundVersion)
	{
		this.refundVersion = refundVersion;
	}

	/**
	 * @return the sellerAddressId
	 */
	public long getSellerAddressId()
	{
		return sellerAddressId;
	}

	/**
	 * @param sellerAddressId
	 *           the sellerAddressId to set
	 */
	public void setSellerAddressId(long sellerAddressId)
	{
		this.sellerAddressId = sellerAddressId;
	}


}
