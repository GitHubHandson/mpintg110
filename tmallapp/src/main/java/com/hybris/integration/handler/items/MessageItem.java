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
package com.hybris.integration.handler.items;

import com.hybris.integration.handler.invocation.Request;



/**
 * Message Item
 */
public abstract class MessageItem
{
	/**
	 * Get Messages Topic
	 *
	 * @return Message type
	 */
	public abstract String getMessageTopic();

	/**
	 * @param request
	 * @return Processing status
	 */
	public abstract boolean excute(Request request);
}
