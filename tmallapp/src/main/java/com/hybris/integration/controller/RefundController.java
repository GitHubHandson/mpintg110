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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hybris.integration.aop.annotation.OpenWriteBackOperation;
import com.hybris.integration.service.datahub.DataHubRefundService;
import com.hybris.integration.service.datahub.DataHubTradeService;
import com.hybris.integration.service.datahub.bean.DatahubRefundListRequest;
import com.hybris.integration.service.datahub.bean.DatahubTradeRequest;
import com.hybris.integration.service.tmall.RefundService;
import com.hybris.integration.service.tmall.TradeService;
import com.hybris.integration.vo.request.RefundAgreeRequest;
import com.hybris.integration.vo.request.RefundListRequest;
import com.hybris.integration.vo.request.RefundRefuseRequest;
import com.hybris.integration.vo.request.ReturngoodsAgreeRequest;
import com.hybris.integration.vo.response.TmallAppResponse;
import com.taobao.api.FileItem;
import com.taobao.api.domain.Refund;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.RpReturngoodsRefuseRequest;
import com.taobao.api.response.RpRefundsAgreeResponse;


/**
 * Seller refund Controller
 */
@RestController
@RequestMapping("/biz/refund")
public class RefundController
{

	@Autowired
	private RefundService refundService;

	@Autowired
	private TradeService tradeService;

	@Autowired
	private DataHubRefundService dhRefundService;

	@Autowired
	private DataHubTradeService dhTradeService;

	@OpenWriteBackOperation
	@RequestMapping(value = "/list/{integrationId}", method = RequestMethod.POST)
	public TmallAppResponse refundReceiveGet(@PathVariable("integrationId") String integrationId,
			@RequestBody RefundListRequest request) throws Exception
	{
		List<Long> refundIds = refundService.refundReceiveGet(integrationId, request);
		if (CollectionUtils.isNotEmpty(refundIds))
		{
			List<Refund> refunds = new ArrayList<Refund>();

			List<Trade> trades = new ArrayList<Trade>();

			//Assembling Trade and Refund the message body sent to Datahub
			for (Long refundId : refundIds)
			{
				Refund refund = refundService.refundGetDetails(integrationId, refundId);
				refunds.add(refund);

				Trade trade = tradeService.getTradeFullInfoByTid(integrationId, refund.getTid());
				trades.add(trade);
			}

			//Send trades data to Datahub
			DatahubTradeRequest tradeRequest = new DatahubTradeRequest();
			tradeRequest.setCurrency(request.getCurrency());
			tradeRequest.setProductCatalogVersion(request.getProductCatalogVersion());
			tradeRequest.setTrades(trades);
			dhTradeService.saveOrders(tradeRequest, integrationId);

			//Send refunds data to Datahub
			DatahubRefundListRequest refundRequest = new DatahubRefundListRequest();
			refundRequest.setRefunds(refunds);
			dhRefundService.saveRefunds(refundRequest, integrationId);
		}
		return new TmallAppResponse();
	}

	@OpenWriteBackOperation
	@RequestMapping(value = "/refuse", method = RequestMethod.POST)
	public TmallAppResponse refundRefuse(@RequestBody RefundRefuseRequest request) throws Exception
	{
		Refund refund = refundService.refundGetDetails(request.getIntegrationId(), request.getRefundId());
		com.taobao.api.request.RefundRefuseRequest taobaoReq = new com.taobao.api.request.RefundRefuseRequest();
		taobaoReq.setRefundId(request.getRefundId());
		taobaoReq.setRefundPhase(request.getRefundPhase());
		taobaoReq.setRefuseMessage(request.getRefuseMessage());
		if (request.getRefuseProof() != null)
		{
			taobaoReq.setRefuseProof(new FileItem("Refuse Proof", request.getRefuseProof()));
		}
		taobaoReq.setRefundVersion(refund.getRefundVersion());

		refundService.refundRefuse(request.getIntegrationId(), taobaoReq);
		return new TmallAppResponse();
	}

	@OpenWriteBackOperation
	@RequestMapping(value = "/agree", method = RequestMethod.POST)
	public TmallAppResponse refundAgree(@RequestBody RefundAgreeRequest request) throws Exception
	{
		Refund refund = refundService.refundGetDetails(request.getIntegrationId(), request.getRefundId());

		request.setVersion(refund.getRefundVersion());
		RpRefundsAgreeResponse response = refundService.refundAgree(request.getIntegrationId(), request);
		return new TmallAppResponse(response.getResults());
	}

	@OpenWriteBackOperation
	@RequestMapping(value = "/returngoodsagree", method = RequestMethod.POST)
	public TmallAppResponse returngoodsAgree(@RequestBody ReturngoodsAgreeRequest request) throws Exception
	{
		Refund refund = refundService.refundGetDetails(request.getIntegrationId(), request.getRefundId());

		request.setRefundVersion(refund.getRefundVersion());
		boolean status = refundService.returngoodsAgree(request.getIntegrationId(), request);

		return new TmallAppResponse(status);
	}

	@OpenWriteBackOperation
	@RequestMapping(value = "/returngoodsrefuse", method = RequestMethod.POST)
	public TmallAppResponse returngoodsRefuse(@RequestBody RefundRefuseRequest request) throws Exception
	{
		Refund refund = refundService.refundGetDetails(request.getIntegrationId(), request.getRefundId());

		RpReturngoodsRefuseRequest taobaoReq = new RpReturngoodsRefuseRequest();

		taobaoReq.setRefundId(request.getRefundId());
		taobaoReq.setRefundPhase(request.getRefundPhase());
		taobaoReq.setRefundVersion(refund.getRefundVersion());
		if (request.getRefuseProof() != null)
		{
			taobaoReq.setRefuseProof(new FileItem("Refuse Proof", request.getRefuseProof()));
		}
		taobaoReq.setRefuseReasonId(request.getRefuseReasonId());

		boolean status = refundService.returngoodsRefuse(request.getIntegrationId(), taobaoReq);

		return new TmallAppResponse(status);
	}
}
