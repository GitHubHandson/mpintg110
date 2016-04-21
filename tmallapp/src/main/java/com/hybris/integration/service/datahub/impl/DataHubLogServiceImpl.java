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
package com.hybris.integration.service.datahub.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.datahub.DataHubLogService;
import com.taobao.api.ApiException;


/**
 * Datahub logServiceImpl
 */
@Service("datahub.logServiceImpl")
public class DataHubLogServiceImpl extends BaseServiceImpl implements DataHubLogService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DataHubLogServiceImpl.class);

	@Value("${tmall.feedback.log}")
	private String feedbackLogPath;

	@Override
	public void recordLog(String businessId, String logPK, String status, String message) throws ApiException, TmallAppException
	{
		LOGGER.trace("begin to push feedback log to Datahub... ");

		String url = datahubServerUrl + feedbackLogPath;
		JsonObject responseResults;

		final JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("actionstatus", status);
		jsonObj.addProperty("actionuuid", logPK);
		jsonObj.addProperty("message", message);
		jsonObj.addProperty("messagesource", "TM");
		responseResults = restTemplateUtil.post(url, jsonObj.toString());

		LOGGER.trace("push result: " + responseResults.get("msg").toString());
		LOGGER.trace("push feedback log to Datahub finished... ");
	}
}
