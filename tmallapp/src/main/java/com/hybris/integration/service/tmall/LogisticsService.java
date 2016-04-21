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
package com.hybris.integration.service.tmall;

import com.hybris.integration.exception.TmallAppException;
import com.taobao.api.ApiException;
import com.taobao.api.request.LogisticsOfflineSendRequest;


/**
 *
 */
public interface LogisticsService
{

	/**
	 * @param integrationId
	 * @param request
	 * @return shipping status
	 * @throws ApiException
	 * @throws TmallAppException
	 */
	public String offlinesend(String integrationId, String marketplaceLogId, LogisticsOfflineSendRequest request)
			throws ApiException, TmallAppException;
}
