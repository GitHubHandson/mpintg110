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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.handler.TradeHandler;
import com.hybris.integration.service.tmall.TradeService;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.util.RestTemplateUtil;
import com.hybris.integration.vo.request.TradeDownloadRequest;
import com.hybris.integration.vo.response.TmallAppResponse;



/**
 * Trade Controller
 */
@RestController
@RequestMapping("/biz/trade")
public class TradeController
{
	@Autowired
	private TradeHandler tradeHanlder;

	@Autowired
	private TradeService tradeService;

	/**
	 * @param integrationId
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/initialazeorders/{integrationId}", method = RequestMethod.POST)
	public TmallAppResponse initializeOrders(@PathVariable("integrationId") final String integrationId,
			@RequestBody final TradeDownloadRequest request) throws Exception
	{
		if (request == null || StringUtils.isEmpty(request.getStatus()))
		{
			throw new TmallAppException(ResponseCode.MISSING_REQUIRED_PARAMS.getCode(), "Missing parameters.");
		}

		request.setIntegrationId(integrationId);
		tradeHanlder.setRequest(request);
		tradeHanlder.execute();

		return new TmallAppResponse();
	}

	@Autowired
	private RestTemplateUtil templateUtil;

	@Value("${datahub.server.host}${tmall.context.path}${tmall.trades.api}")
	private String datahubServicePath;

	/**
	 * @param data
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "/integrationtest", method = RequestMethod.POST)
	public TmallAppResponse integrationTest(@RequestBody final TradeDownloadRequest data) throws Exception
	{
		JsonObject results = new JsonObject();
		if (data != null)
		{
			final String jsonContent = CommonUtils.getGsonByBuilder(false).toJson(data);
			results = templateUtil.post(datahubServicePath, jsonContent);
		}

		return new TmallAppResponse(results);
	}
}
