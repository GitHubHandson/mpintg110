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


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.service.tmall.APIService;
import com.hybris.integration.service.tmall.RefundService;
import com.hybris.integration.util.CommonUtils;
import com.hybris.integration.util.ResponseCode;
import com.hybris.integration.vo.request.RefundAgreeRequest;
import com.hybris.integration.vo.request.RefundListRequest;
import com.hybris.integration.vo.request.ReturngoodsAgreeRequest;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Refund;
import com.taobao.api.domain.RefundMappingResult;
import com.taobao.api.internal.util.StringUtils;
import com.taobao.api.request.RefundGetRequest;
import com.taobao.api.request.RefundRefuseRequest;
import com.taobao.api.request.RefundsReceiveGetRequest;
import com.taobao.api.request.RpRefundsAgreeRequest;
import com.taobao.api.request.RpReturngoodsAgreeRequest;
import com.taobao.api.request.RpReturngoodsRefuseRequest;
//hong.li03%40sap.com@stash.hybris.com/scm/saplabs/tmallapp.git
import com.taobao.api.response.RefundGetResponse;
import com.taobao.api.response.RefundRefuseResponse;
import com.taobao.api.response.RefundsReceiveGetResponse;
import com.taobao.api.response.RpRefundsAgreeResponse;
import com.taobao.api.response.RpReturngoodsAgreeResponse;
import com.taobao.api.response.RpReturngoodsRefuseResponse;


/**
 * Seller Refund
 */
@Service("tmall.refundServiceImpl")
public class RefundServiceImpl extends BaseServiceImpl implements RefundService
{
	private static Logger LOGGER = LoggerFactory.getLogger(RefundServiceImpl.class);

	@Autowired
	private APIService apiService;

	@Override
	public List<Long> refundReceiveGet(String integrationId, RefundListRequest request) throws ApiException, TmallAppException
	{
		RefundsReceiveGetRequest req = new RefundsReceiveGetRequest();

		req.setFields("refund_id");
		req.setStatus(request.getStatus());
		req.setStartModified(StringUtils.parseDateTime(request.getStartCreated()));
		req.setEndModified(CommonUtils.lastSecondOfTheDayTime(request.getEndCreated()));
		req.setPageSize(40L);
		req.setUseHasNext(true);

		RefundsReceiveGetResponse response = new RefundsReceiveGetResponse();

		long pageNo = 1;

		List<Long> refundIds = new ArrayList<Long>();
		do
		{
			req.setPageNo(pageNo);

			response = getClient(integrationId).execute(req, getToken(integrationId));

			if (org.apache.commons.lang3.StringUtils.isNotEmpty(response.getErrorCode()))
			{
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), "Response code from tmall = "
						+ response.getSubCode() + "; Response msg from tmall" + response.getSubMsg());
			}
			List<Refund> refunds = response.getRefunds();

			if (response.getHasNext() == false && refunds == null)
			{
				break;
			}

			for (Refund refund : refunds)
			{
				refundIds.add(refund.getRefundId());
			}

			pageNo++;
		}
		while (response.getHasNext());

		LOGGER.trace("Query to the data size :" + refundIds.size());

		return refundIds;
	}

	@Override
	public Refund refundGetDetails(String integrationId, Long refundId) throws ApiException, TmallAppException
	{
		Refund refund = null;
		RefundGetRequest req = new RefundGetRequest();
		String fieldsParam = apiService.getTmallDomainFieldByClassType(Refund.class, null);

		req.setFields(fieldsParam);
		req.setRefundId(refundId);
		RefundGetResponse response = getClient(integrationId).execute(req, getToken(integrationId));

		if (response != null && StringUtils.isEmpty(response.getErrorCode()))
		{
			refund = response.getRefund();
		}
		else
		{
			response = getClient(integrationId).execute(req, getToken(integrationId));

			if (response != null && StringUtils.isEmpty(response.getErrorCode()))
			{
				refund = response.getRefund();
			}
			else
			{
				LOGGER.error(response.getBody());
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
			}
		}
		return refund;
	}

	@Override
	public RefundRefuseResponse refundRefuse(String integrationId, RefundRefuseRequest request) throws ApiException,
			TmallAppException
	{

		RefundRefuseResponse response = getClient(integrationId).execute(request, getToken(integrationId));

		if (response != null && !StringUtils.isEmpty(response.getErrorCode()))
		{
			response = getClient(integrationId).execute(request, getToken(integrationId));

			if (response != null && !StringUtils.isEmpty(response.getErrorCode()))
			{
				LOGGER.error("Tmall API taobao.refund.refuse failed request:" + response.getBody());
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
			}
		}
		return response;
	}

	@Override
	public RpRefundsAgreeResponse refundAgree(String integrationId, RefundAgreeRequest request) throws ApiException,
			TmallAppException
	{
		RpRefundsAgreeRequest req = new RpRefundsAgreeRequest();
		req.setCode(request.getCode());
		//Refund information, Format: refund_id | amount | version | phase
		req.setRefundInfos(request.getRefundId() + "|" + request.getAmount() + "|" + request.getVersion());
		RpRefundsAgreeResponse response = getClient(integrationId).execute(req, getToken(integrationId));

		if (response != null && !StringUtils.isEmpty(response.getMsgCode()) && !"OP_SUCC".equals(response.getMsgCode()))
		{
			LOGGER.error("Tmall API taobao.rp.refunds.agree failed request:" + response.getBody());

			StringBuffer errorMsg = new StringBuffer();
			for (RefundMappingResult result : response.getResults())
			{
				errorMsg.append("refundId=" + result.getRefundId() + ",message=" + result.getMessage() + "|");
			}
			throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), errorMsg.toString(), response.getResults());
		}
		if (response != null && !StringUtils.isEmpty(response.getErrorCode()))
		{
			LOGGER.error("Tmall API taobao.rp.refunds.agree failed request:" + response.getBody());
			throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
		}
		return response;
	}

	@Override
	public Boolean returngoodsAgree(String integrationId, ReturngoodsAgreeRequest request) throws ApiException, TmallAppException
	{
		RpReturngoodsAgreeRequest req = new RpReturngoodsAgreeRequest();
		BeanUtils.copyProperties(request, req);
		RpReturngoodsAgreeResponse response = getClient(integrationId).execute(req, getToken(integrationId));

		if (response != null && !StringUtils.isEmpty(response.getErrorCode()))
		{
			response = getClient(integrationId).execute(req, getToken(integrationId));

			if (response != null && !StringUtils.isEmpty(response.getErrorCode()))
			{
				LOGGER.error("Tmall API taobao.rp.returngoods.agree failed request:" + response.getBody());
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
			}
		}
		return response.isSuccess();
	}

	@Override
	public Boolean returngoodsRefuse(String integrationId, RpReturngoodsRefuseRequest request) throws ApiException,
			TmallAppException
	{
		RpReturngoodsRefuseResponse response = getClient(integrationId).execute(request, getToken(integrationId));
		if (response != null && !StringUtils.isEmpty(response.getErrorCode()))
		{
			response = getClient(integrationId).execute(request, getToken(integrationId));

			if (response != null && !StringUtils.isEmpty(response.getErrorCode()))
			{
				LOGGER.error("Tmall API taobao.rp.returngoods.refuse failed request:" + response.getBody());
				throw new TmallAppException(ResponseCode.REQUEST_TMALL_ERROR.getCode(), response.getSubMsg());
			}
		}
		return response.isSuccess();
	}
}
