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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hybris.integration.aop.annotation.OpenWriteBackOperation;
import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.handler.ReceiveMessagesHandler;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.vo.request.TmcMessageRequest;
import com.hybris.integration.vo.response.TmallAppResponse;


/**
 * Tmall Tmc Message Controller
 */
@RestController
@RequestMapping("/biz/tmc")
public class TmcMessageController
{
	@Autowired
	private ReceiveMessagesHandler msgHanlder;

	/**
	 * @param integrationId
	 * @param request
	 * @return String result
	 * @throws Exception
	 */
	@RequestMapping(value = "/openlistener/{integrationId}")
	@OpenWriteBackOperation
	public TmallAppResponse openListener(@PathVariable("integrationId") final String integrationId,
			@RequestBody final TmcMessageRequest request) throws Exception
	{

		if (request == null || StringUtils.isEmpty(request.getStatus()))
		{
			throw new TmallAppException(ResponseCode.MISSING_REQUIRED_PARAMS.getCode(), "Missing parameters.");
		}

		msgHanlder.setIntegrationId(integrationId);
		msgHanlder.setTmcMessageRequest(request);
		msgHanlder.execute();

		return new TmallAppResponse();
	}
}
