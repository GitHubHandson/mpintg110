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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hybris.integration.aop.annotation.OpenWriteBackOperation;
import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.tmall.LogisticsService;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.vo.request.ConfirmConsignmentRequest;
import com.hybris.integration.vo.response.TmallAppResponse;
import com.taobao.api.request.LogisticsOfflineSendRequest;


/**
 * Logistics Controller
 */
@RestController
@RequestMapping("/biz/logistics")
public class LogisticsController
{

	@Autowired
	private LogisticsService offlinesendService;

	/**
	 * @param integrationId
	 * @param ccRequest
	 * @throws Exception
	 */
	@OpenWriteBackOperation
	@RequestMapping(value = "/logisticsofflinesend", method = RequestMethod.POST)
	public TmallAppResponse confirmConsignments(@RequestBody final ConfirmConsignmentRequest ccRequest) throws Exception
	{
		if (null == ccRequest.getTid() && null == ccRequest.getOutSid() && null == ccRequest.getCompanyCode())
		{
			throw new TmallAppException(ResponseCode.MISSING_REQUIRED_PARAMS.getCode(), "Missing mandatory parameters.");
		}

		LogisticsOfflineSendRequest losRequest = new LogisticsOfflineSendRequest();
		//		BeanUtils.copyProperties(losRequest, ccRequest);
		losRequest.setSubTid(ccRequest.getSubTid());
		losRequest.setTid(ccRequest.getTid());
		losRequest.setIsSplit(ccRequest.getIsSplit());
		losRequest.setOutSid(ccRequest.getOutSid());
		losRequest.setCompanyCode(ccRequest.getCompanyCode());
		losRequest.setSenderId(ccRequest.getSenderId());
		losRequest.setCancelId(ccRequest.getCancelId());
		losRequest.setFeature(ccRequest.getFeature());
		losRequest.setSellerIp(ccRequest.getSellerIp());

		String status = offlinesendService.offlinesend(ccRequest.getIntegrationId(), ccRequest.getMarketplaceLogId(), losRequest);
		return new TmallAppResponse(status);
	}

}
