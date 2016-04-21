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
package com.hybris.integration.controller.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.vo.response.TmallAppResponse;


/**
 * Exception Handling System
 */
@ControllerAdvice
public class ApiExceptionHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(TmallAppException.class)
	@ResponseBody
	public Object handleTmallAppException(TmallAppException ex)
	{
		return parseErrorMessage(ex);
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public TmallAppResponse handleUnexpectedServerError(RuntimeException ex)
	{
		TmallAppResponse response = new TmallAppResponse();
		response.setCode(ResponseCode.INTERNAL_SERVER_ERROR.getCode());
		response.setMsg(ex.getClass().toString() + ":" + ex.getMessage());
		LOGGER.error(ex.getMessage(), ex);
		return response;
	}

	private TmallAppResponse parseErrorMessage(RuntimeException ex)
	{
		return CommonUtils.getGsonByBuilder(false).fromJson(ex.getMessage(), TmallAppResponse.class);
	}
}
