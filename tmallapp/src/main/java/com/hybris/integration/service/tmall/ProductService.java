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
import com.taobao.api.response.ItemUpdateDelistingResponse;
import com.taobao.api.response.ItemUpdateListingResponse;


public interface ProductService
{

	/**
	 * @param integrationId
	 * @param productId
	 * @return response
	 * @throws ApiException
	 */
	public ItemUpdateDelistingResponse deListing(String integrationId, String productId) throws TmallAppException, ApiException;

	/**
	 * @param integrationId
	 * @param productId
	 * @param listingNum
	 * @return response
	 * @throws ApiException
	 */
	public ItemUpdateListingResponse listing(String integrationId, String productId, Long listingNum) throws ApiException,
			TmallAppException;


}
