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
package com.hybris.integration.service.datahub;

import com.hybris.integration.service.datahub.bean.DatahubTradeRequest;


/**
 * Datahub OrderService
 */
public interface DataHubTradeService
{
	/**
	 * @param tradeRequest
	 * @param integrationId
	 * @return boolean
	 */
	public Boolean saveOrders(DatahubTradeRequest tradeRequest, String integrationId);
}
