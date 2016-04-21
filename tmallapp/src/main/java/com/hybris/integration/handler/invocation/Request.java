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
package com.hybris.integration.handler.invocation;

import java.io.Serializable;

import com.hybris.integration.vo.request.TmcMessageRequest;


/**
 * Tmall message service processing javabean
 */
public class Request implements Serializable
{

	private static final long serialVersionUID = 1L;

	private String integrationId;

	private String topic;

	private String content;

	private TmcMessageRequest tmcMessageCriteria;

	/**
	 * @return integrationId
	 */
	public String getIntegrationId()
	{
		return integrationId;
	}

	/**
	 * @param integrationId
	 */
	public void setIntegrationId(final String integrationId)
	{
		this.integrationId = integrationId;
	}

	/**
	 * @return topic
	 */
	public String getTopic()
	{
		return topic;
	}

	/**
	 * @param topic
	 */
	public void setTopic(final String topic)
	{
		this.topic = topic;
	}

	/**
	 * @return content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content
	 */
	public void setContent(final String content)
	{
		this.content = content;
	}

	/**
	 * @return tmcMessageCriteria
	 */
	public TmcMessageRequest getTmcMessageCriteria()
	{
		return tmcMessageCriteria;
	}

	/**
	 * @param tmcMessageCriteria
	 */
	public void setTmcMessageCriteria(final TmcMessageRequest tmcMessageCriteria)
	{
		this.tmcMessageCriteria = tmcMessageCriteria;
	}

}
