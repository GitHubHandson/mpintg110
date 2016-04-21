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
package com.hybris.integration.service.tmall;

import java.util.List;

import com.hybris.integration.exception.TmallAppException;
import com.hybris.integration.vo.request.RefundAgreeRequest;
import com.hybris.integration.vo.request.RefundListRequest;
import com.hybris.integration.vo.request.ReturngoodsAgreeRequest;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Refund;
import com.taobao.api.request.RefundRefuseRequest;
import com.taobao.api.request.RpReturngoodsRefuseRequest;
import com.taobao.api.response.RefundRefuseResponse;
import com.taobao.api.response.RpRefundsAgreeResponse;


/**
 * Seller Refund
 */
public interface RefundService
{

	/**
	 * Seller received the refund list
	 * 
	 * @param integrationId
	 * @param request
	 * @return
	 * @throws ApiException
	 * @throws AppException
	 */
	public List<Long> refundReceiveGet(String integrationId, RefundListRequest request) throws ApiException, TmallAppException;

	/**
	 * Query refund details
	 *
	 * @param integrationId
	 * @param refundId
	 * @return
	 * @throws ApiException
	 * @throws AppException
	 */
	public Refund refundGetDetails(String integrationId, Long refundId) throws ApiException, TmallAppException;

	/**
	 * The seller refused to refund
	 * 
	 * @param integrationId
	 * @param request
	 * @return
	 */
	public RefundRefuseResponse refundRefuse(String integrationId, RefundRefuseRequest request) throws ApiException,
			TmallAppException;

	/**
	 * The seller agreed to refund
	 * 
	 * @param integrationId
	 * @param request
	 * @return
	 * @throws ApiException
	 * @throws TmallAppException
	 */
	public RpRefundsAgreeResponse refundAgree(String integrationId, RefundAgreeRequest request) throws ApiException,
			TmallAppException;

	/**
	 * The seller agreed to return
	 * 
	 * @param integrationId
	 * @param request
	 * @return
	 * @throws ApiException
	 * @throws TmallAppException
	 */
	public Boolean returngoodsAgree(String integrationId, ReturngoodsAgreeRequest request) throws ApiException, TmallAppException;

	/**
	 * The seller refused to return
	 * 
	 * @param integrationId
	 * @param request
	 * @return
	 * @throws ApiException
	 * @throws TmallAppException
	 */
	public Boolean returngoodsRefuse(String integrationId, RpReturngoodsRefuseRequest request) throws ApiException,
			TmallAppException;

}
