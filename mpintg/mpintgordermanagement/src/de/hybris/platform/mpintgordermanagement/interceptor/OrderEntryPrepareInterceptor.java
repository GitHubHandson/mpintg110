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
package de.hybris.platform.mpintgordermanagement.interceptor;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.marketplaceintegration.enums.RefundStatus;
import de.hybris.platform.marketplaceintegration.model.TmallOrderEntryModel;
import de.hybris.platform.marketplaceintegration.model.TmallOrderModel;
import de.hybris.platform.marketplaceintegration.utils.MarketplaceintegrationbackofficeLogUtil;
import de.hybris.platform.mpintgordermanagement.enums.RefundType;
import de.hybris.platform.mpintgordermanagement.service.CancelOrderService;
import de.hybris.platform.mpintgordermanagement.service.ConfirmShipService;
import de.hybris.platform.mpintgordermanagement.service.CreateReturnService;
import de.hybris.platform.mpintgordermanagement.service.ReturnRefundService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hybris.backoffice.model.TmallRefundRequestModel;


public class OrderEntryPrepareInterceptor implements PrepareInterceptor<OrderEntryModel>
{
	@Autowired
	private ConfirmShipService confirmShipService;

	@Autowired
	private CancelOrderService cancelOrderService;

	@Autowired
	private CreateReturnService createReturnService;

	@Resource
	protected FlexibleSearchService flexibleSearchService;

	@Autowired
	private MarketplaceintegrationbackofficeLogUtil logUtil;

	@Autowired
	private ReturnRefundService returnRefundService;

	public static final String WAIT_BUYER_CONFIRM_GOODS = "WAIT_BUYER_CONFIRM_GOODS";

	public static final String CREATE_RETURN_FLAG = "WAIT_SELLER_CONFIRM_GOODS";

	public static final String RETURN_REFUND_SUCCESS_FLAG = "SUCCESS";

	private static Logger log = Logger.getLogger(OrderEntryPrepareInterceptor.class);

	private static ThreadLocal<HashMap<String, String>> contextThreadLocal = new ThreadLocal<HashMap<String, String>>()
	{
		@Override
		protected HashMap<String, String> initialValue()
		{
			return new HashMap<>();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.PrepareInterceptor#onPrepare(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onPrepare(OrderEntryModel orderEntrymodel, InterceptorContext ctx) throws InterceptorException
	{
		String threadLocalKey = "entriedOnce-" + orderEntrymodel.getEntryNumber();
		HashMap<String, String> contextThreadLocalMap = contextThreadLocal.get();
		if (contextThreadLocalMap.containsKey(threadLocalKey))
		{
			return;
		}
		contextThreadLocalMap.put(threadLocalKey, "true");

		if (orderEntrymodel instanceof TmallOrderEntryModel)
		{
			final TmallOrderEntryModel tmallOrderEntryModel = (TmallOrderEntryModel) orderEntrymodel;
			try
			{
				onPrepareTmallOrderEntry(tmallOrderEntryModel);
			}
			catch (Exception e)
			{
				log.error("Handle order entry status mapping service failed for " + orderEntrymodel, e);
				logMessage(Boolean.FALSE, "Sync status to hybris", tmallOrderEntryModel, "Sync status to hybris failed due to unknown reason:"
						+ e.getMessage());
			}
		}
		contextThreadLocalMap.remove(threadLocalKey);
	}

	/*
	 * handler tmall order entry
	 */
	private void onPrepareTmallOrderEntry(TmallOrderEntryModel tmallOrderEntryModel)
	{
		if (WAIT_BUYER_CONFIRM_GOODS.equals(tmallOrderEntryModel.getEntryStatus().getCode()))
		{
			// for tmall order, one entry should in one consigmment
			for (final ConsignmentEntryModel consignmentEntryModel : tmallOrderEntryModel.getConsignmentEntries())
			{
				if (consignmentEntryModel.getConsignment().getStatus() != ConsignmentStatus.SHIPPED)
				{
					boolean isShipped = confirmShipService.confirmShip(consignmentEntryModel);
					tmallOrderEntryModel.setWaitMarketPlaceResponse(false);
					log.info("Do confirm ship for consignmentEntryModel " + consignmentEntryModel + ",success:" + isShipped);
					logMessage(isShipped, "Confirm Ship", tmallOrderEntryModel, "Do confirm ship");
				}
			}
		}

		//return happened when the order entry status is WAIT_BUYER_CONFIRM_GOODS
		String refundStatus = String.valueOf(tmallOrderEntryModel.getRefundStatus());
		switch (refundStatus)
		{
			case CREATE_RETURN_FLAG:

				//approve return request,do create return process
				if (createReturnService.isReturnable(tmallOrderEntryModel))
				{
					boolean isSuccess = createReturnService.executeReturnProcess(tmallOrderEntryModel, RefundReason.PRICEMATCH);
					log.info("Do Return Refund for " + tmallOrderEntryModel + ",success:" + isSuccess);
					logMessage(isSuccess, "Sync status to hybris", tmallOrderEntryModel, "create return process for "
							+ tmallOrderEntryModel);
				}
				break;

			case RETURN_REFUND_SUCCESS_FLAG:
				//auto refund
				TmallRefundRequestModel tmallRefundRequestModel = findTmallRefundRequestByEntry(tmallOrderEntryModel);
				doReturnRefund(tmallOrderEntryModel, tmallRefundRequestModel);
				break;
		}
	}

	private boolean doReturnRefund(TmallOrderEntryModel tmallOrderEntryModel, TmallRefundRequestModel tmallRefundRequestModel)
	{
		boolean isSuccess = false;

		if (log.isInfoEnabled())
		{
			log.info("Tmall OrderEntry " + tmallOrderEntryModel + " refund status is "
					+ String.valueOf(tmallOrderEntryModel.getRefundStatus()) + ", request type is"
					+ tmallRefundRequestModel.getRefundType());
		}

		if (RefundType.REFUND_ONLY.equals(tmallRefundRequestModel.getRefundType()))
		{
			if (!isContainsStatus(tmallOrderEntryModel, ConsignmentStatus.CANCELLED)
					&& !isContainsStatus(tmallOrderEntryModel, ConsignmentStatus.SHIPPED))
			{
				// do cancel order,consignment not shipped
				isSuccess = cancelOrderService.cancelEntireOrderEntry(tmallOrderEntryModel, CancelReason.CUSTOMERREQUEST);
				log.info("Do Refund Only for " + tmallRefundRequestModel + ",success:" + isSuccess);
				logMessage(isSuccess, "Sync status to hybris", tmallOrderEntryModel, "Change hybris consignment status to shiped ");
			}

		}
		else if (RefundType.RETURN_REFUND.equals(tmallRefundRequestModel.getRefundType()))
		{
			// do manual refund
			final List<ReturnRequestModel> returnRequestList = tmallOrderEntryModel.getOrder().getReturnRequests();
			Optional<ReturnRequestModel> returnRequest = returnRequestList.stream().filter(e -> {
				for (ReturnEntryModel returnEntryModel : e.getReturnEntries())
				{
					return returnEntryModel.getOrderEntry() == tmallOrderEntryModel;
				}
				return false;
			}).findFirst();

			if (returnRequest.isPresent())
			{
				isSuccess = returnRefundService.manualRefund(returnRequest.get());
				log.info("Do manual refund for " + tmallRefundRequestModel + ",success:" + isSuccess);
				logMessage(isSuccess, "Sync status to hybris", tmallOrderEntryModel, "Do manual refund for return request"
						+ returnRequest);
			}
		}
		else
		{
			log.error(tmallOrderEntryModel + " refund status is " + String.valueOf(tmallOrderEntryModel.getRefundStatus())
					+ ",but can not find refund request");
			logMessage(isSuccess, "Sync status to hybris", tmallOrderEntryModel, "Can not found tmall refund request");
		}
		return isSuccess;
	}

	private void logMessage(boolean isSuccess, String action, TmallOrderEntryModel tmallOrderEntryModel, String message)
	{
		String logFlag = isSuccess ? "SUCCESS" : "ERROR";
		TmallOrderModel tmallOrderModel = (TmallOrderModel) tmallOrderEntryModel.getOrder();
		logUtil.addMarketplaceOrderLog(logFlag, tmallOrderEntryModel.getOid(), "Sync status to hybris", tmallOrderModel,
				logUtil.getUUID(), message);
	}

	private boolean isContainsStatus(TmallOrderEntryModel tmallOrderEntryModel, ConsignmentStatus status)
	{
		return tmallOrderEntryModel.getConsignmentEntries().stream().anyMatch(e -> e.getConsignment().getStatus() == status);
	}


	private TmallRefundRequestModel findTmallRefundRequestByEntry(TmallOrderEntryModel tmallOrderEntryModel)
	{
		final String refundSql = "SELECT {" + TmallRefundRequestModel.PK + "} FROM {" + TmallRefundRequestModel._TYPECODE + "} "
				+ "WHERE {" + TmallRefundRequestModel.TMALLORDERENTRY + "}=?orderEntryPK";

		if (null == tmallOrderEntryModel.getPk())
		{
			log.error("order entry pk is null, can not find TmallRefundRequestModel for tmallOrderEntryModel "
					+ tmallOrderEntryModel);
			return new TmallRefundRequestModel();
		}

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(refundSql, Collections.singletonMap("orderEntryPK",
				tmallOrderEntryModel.getPk()));
		final SearchResult<TmallRefundRequestModel> searchResult = flexibleSearchService.search(fsq);
		
		// filter out RefundStatus.CLOSED
		List<TmallRefundRequestModel> filteredSearchResult = searchResult.getResult().stream().filter(e->!e.getRefundStatus().equals(RefundStatus.CLOSED)).collect(Collectors.toList());
		
		if (CollectionUtils.isEmpty(filteredSearchResult) || filteredSearchResult.size() > 1)
		{
			log.error("can not find TmallRefundRequestModel for tmallOrderEntryModel " + tmallOrderEntryModel + ",result size "
					+ filteredSearchResult.size());

			//return empty object
			return new TmallRefundRequestModel();
		}
		return filteredSearchResult.get(0);
	}

	/**
	 * @param returnRefundService
	 *           the returnRefundService to set
	 */
	public void setReturnRefundService(ReturnRefundService returnRefundService)
	{
		this.returnRefundService = returnRefundService;
	}
}
