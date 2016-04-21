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

import org.springframework.stereotype.Component;

import com.hybris.integration.exception.TmallAppException;
import com.taobao.api.ApiException;



/**
 * Datahub DHLogService
 */
@Component
public interface DataHubLogService
{

	public final String STATUS_SUCCEED = "SUCCESS";
	public final String STATUS_FAILURE = "FAILURE";
	public final String STATUS_ERROR = "ERROR";

	/**
	 * @param businessId
	 * @param logPK
	 * @param status
	 * @param message
	 */
	public void recordLog(String businessId, String marketplaceLog, String status, String message) throws ApiException,
			TmallAppException;
}
