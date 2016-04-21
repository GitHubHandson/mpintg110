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

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.datahub.bean.DatahubRefundListRequest;



/**
 * Seller Refunds Datahub Service
 */
public interface DataHubRefundService
{
	public Boolean saveRefunds(DatahubRefundListRequest dhRefundRequest, String integrationId) throws TmallAppException;
}
