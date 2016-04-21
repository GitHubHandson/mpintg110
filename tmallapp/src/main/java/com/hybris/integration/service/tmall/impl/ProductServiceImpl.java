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
package com.hybris.integration.service.tmall.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.tmall.APIService;
import com.hybris.integration.service.tmall.ProductService;
import com.hybris.integration.util.ResponseCode;
import com.taobao.api.ApiException;
import com.taobao.api.request.ItemUpdateDelistingRequest;
import com.taobao.api.request.ItemUpdateListingRequest;
import com.taobao.api.response.ItemUpdateDelistingResponse;
import com.taobao.api.response.ItemUpdateListingResponse;


/**
 * ProductServiceImpl
 */
@Service("tmall.productServiceImpl")
public class ProductServiceImpl extends BaseServiceImpl implements ProductService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenServiceImpl.class);
	@Autowired
	private APIService apiService;

	@Override
	public ItemUpdateDelistingResponse deListing(String integrationId, String productId) throws TmallAppException, ApiException
	{
		LOGGER.info("Product {" + integrationId + "} delist starting...");
		ItemUpdateDelistingRequest request = new ItemUpdateDelistingRequest();
		request.setNumIid(Long.valueOf(productId));
		final ItemUpdateDelistingResponse response = getClient(integrationId).execute(request, getToken(integrationId));
		if (response != null && StringUtils.isEmpty(response.getErrorCode()))
		{
			LOGGER.info("Product {" + productId + "} delist finished successfully...");
		}
		else
		{
			LOGGER.error("Tmall API item.update.delisting failed request:" + response.getBody());
			LOGGER.error("Product {" + productId + "} delist failure...");
			throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
		}
		return response;
	}

	@Override
	public ItemUpdateListingResponse listing(String integrationId, String productId, Long listingNum) throws ApiException,
			TmallAppException
	{
		LOGGER.info("Product {" + integrationId + "} list starting...");
		ItemUpdateListingRequest request = new ItemUpdateListingRequest();
		request.setNumIid(Long.valueOf(productId));
		request.setNum(listingNum);
		final ItemUpdateListingResponse response = getClient(integrationId).execute(request, getToken(integrationId));
		if (response != null && StringUtils.isEmpty(response.getErrorCode()))
		{
			LOGGER.info("Product {" + productId + "} list finished successfully...");
			LOGGER.info("Listed num: " + listingNum);
		}
		else
		{
			LOGGER.error("Tmall API item.update.isting failed request:" + response.getBody());
			LOGGER.error("Product {" + productId + "} list failure...");
			throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
		}
		return response;
	}
}
