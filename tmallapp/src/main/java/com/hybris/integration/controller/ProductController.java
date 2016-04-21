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
package com.hybris.integration.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.tmall.ProductService;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.vo.response.TmallAppResponse;


/**
 * Product Controller
 */
@RestController
@RequestMapping("/biz/product")
public class ProductController
{

	@Autowired
	private ProductService productService;

	/**
	 * @param integrationId
	 * @param productId
	 * @param num
	 * @throws Exception
	 */
	@RequestMapping(value = "/productlisting", method = RequestMethod.POST)
	public TmallAppResponse listing(final HttpServletRequest request) throws Exception
	{

		final String requestBody = IOUtils.toString(request.getInputStream(), "utf-8");
		if (StringUtils.isEmpty(requestBody))
		{
			throw new TmallAppException(ResponseCode.REQUEST_BODY_IS_EMPTY.getCode(), ResponseCode.REQUEST_BODY_IS_EMPTY.getValue());
		}
		final JsonElement jsonElement = new JsonParser().parse(requestBody);
		final JsonObject jo = jsonElement.getAsJsonObject();

		if (jo.get("integrationId") == null || jo.get("productId") == null || jo.get("num") == null)
		{
			throw new TmallAppException(ResponseCode.MISSING_REQUIRED_PARAMS.getCode(),
					ResponseCode.MISSING_REQUIRED_PARAMS.getValue());
		}

		final String integrationId = jo.get("integrationId").getAsString();

		final String productId = jo.get("productId").getAsString();

		final Long num = jo.get("num").getAsLong();

		productService.listing(integrationId, productId, num);

		return new TmallAppResponse();
	}

	/**
	 * @param integrationId
	 * @throws Exception
	 */
	@RequestMapping(value = "/productdelisting", method = RequestMethod.POST)
	public TmallAppResponse deListing(final HttpServletRequest request) throws Exception
	{

		final String requestBody = IOUtils.toString(request.getInputStream(), "utf-8");
		if (StringUtils.isEmpty(requestBody))
		{
			throw new TmallAppException(ResponseCode.REQUEST_BODY_IS_EMPTY.getCode(), ResponseCode.REQUEST_BODY_IS_EMPTY.getValue());
		}
		final JsonElement jsonElement = new JsonParser().parse(requestBody);
		final JsonObject jo = jsonElement.getAsJsonObject();

		if (jo.get("integrationId") == null || jo.get("productId") == null)
		{
			throw new TmallAppException(ResponseCode.MISSING_REQUIRED_PARAMS.getCode(),
					ResponseCode.MISSING_REQUIRED_PARAMS.getValue());
		}

		final String integrationId = jo.get("integrationId").getAsString();

		final String productId = jo.get("productId").getAsString();

		productService.deListing(integrationId, productId);

		return new TmallAppResponse();
	}

}
