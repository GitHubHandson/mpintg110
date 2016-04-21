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
 *
 *
 */
package com.hybris.integration.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.datahub.DataHubLogService;


@Aspect
@Component
public class WriteBackOperationStateAspect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteBackOperationStateAspect.class);
	@Autowired
	private DataHubLogService dhLogService;

	@Autowired
	private HttpServletRequest request;

	@Pointcut("@annotation(com.hybris.integration.aop.annotation.OpenWriteBackOperation)")
	private void pointcutService()
	{
	}

	@Around("pointcutService()")
	public Object aroundServiceActions(ProceedingJoinPoint pjp) throws Throwable
	{
		LOGGER.trace("AOP started..... ");
		String marketplaceLogUUID = request.getParameter("marketplaceLogUUID");
		Object result = null;
		try
		{
			result = pjp.proceed();
		}
		catch (Throwable e)
		{
			try
			{
				String executionMsg = null;
				String status = null;
				if (e instanceof TmallAppException)
				{
					JsonParser parser = new JsonParser();
					JsonObject jsonObject = parser.parse(e.getMessage()).getAsJsonObject();
					if (jsonObject.get("msg") != null)
					{
						executionMsg = jsonObject.get("msg").getAsString();
					}
					status = DataHubLogService.STATUS_FAILURE;
					dhLogService.recordLog(null, marketplaceLogUUID, status, executionMsg);
				}
			}
			catch (Exception e1)
			{
				LOGGER.error(e1.getMessage(), e1);
			}
			throw e;
		}

		dhLogService.recordLog(null, marketplaceLogUUID, DataHubLogService.STATUS_SUCCEED, "Success");
		LOGGER.trace("End AOP execution..... ");
		return result;
	}
}
