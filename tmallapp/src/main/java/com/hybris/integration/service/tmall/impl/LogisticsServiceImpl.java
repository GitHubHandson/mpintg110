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

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.datahub.impl.DataHubLogServiceImpl;
import com.hybris.integration.service.tmall.LogisticsService;
import com.hybris.integration.util.ResponseCode;
import com.taobao.api.ApiException;
import com.taobao.api.request.LogisticsOfflineSendRequest;
import com.taobao.api.response.LogisticsOfflineSendResponse;


/**
 * LogisticsServiceImpl
 */
@Service("tmall.logisticsServiceImpl")
public class LogisticsServiceImpl extends BaseServiceImpl implements LogisticsService
{
	@Resource
	@Autowired
	private DataHubLogServiceImpl logService;
	private static final Logger LOGGER = LoggerFactory.getLogger(LogisticsServiceImpl.class);

	@Override
	public String offlinesend(String integrationId, String marketplaceLogId, LogisticsOfflineSendRequest request)
			throws ApiException
	{
		LogisticsOfflineSendResponse rsp = getClient(integrationId).execute(request, getToken(integrationId));
		String status = null;
		if (rsp != null && StringUtils.isEmpty(rsp.getErrorCode()))
		{
			status = rsp.getShipping().getIsSuccess() + "";
		}
		else
		{
			LOGGER.error(rsp.getBody());
			throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), rsp.getSubMsg());
		}
		return status;
	}

}
