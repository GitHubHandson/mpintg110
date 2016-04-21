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
package de.hybris.platform.mpintgordermanagement.service;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.process.BusinessProcessException;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class ReturnRefundService
{
	protected static final String MANUAL_REFUND_IN_SOCKET = "manualRefund";
	protected static final String AUTO_REFUND_IN_SOCKET = "autoRefund";
	protected static final String CANCEL_REFUND_IN_SOCKET = "cancelRefund";
	protected static final String MANUAL_TAX_REVERSE_IN_SOCKET = "manualTaxReverse";

	protected static final String CONFIRM_OR_CANCEL_REFUND_ACTION_EVENT_NAME = "ConfirmOrCancelRefundEvent";
	protected static final String FAIL_CAPTURE_ACTION_EVENT_NAME = "FailCaptureActionEvent";
	protected static final String FAIL_TAX_REVERSE_EVENT_NAME = "FailTaxReverseEvent";

	protected static final String TAX_REVERSE_CHOICE = "taxReverse";
	protected static final String CAPTURE_REFUND_CHOICE = "captureRefund";
	protected static final String CANCEL_REFUND_CHOICE = "cancelReturn";
	protected static final String BYPASS_CAPTURE_CHOICE = "bypassCapture";

	protected static final String MANUAL_REFUND_FAILURE = "ordermanagementbackoffice.returnrequest.manual.refund.failure";

	protected static final String AUTOMATIC_REFUND_FAILURE = "ordermanagementbackoffice.returnrequest.automatic.refund.failure";
	protected static final String AUTOMATIC_REFUND_SUCCESS = "ordermanagementbackoffice.returnrequest.automatic.refund.success";

	private static final Logger LOG = LoggerFactory.getLogger(ReturnRefundService.class);

	@Autowired
	private WarehousingBusinessProcessService<ReturnRequestModel> returnBusinessProcessService;

	@Autowired
	private ModelService modelService;


	/**
	 * manual refund done on a specific return request
	 *
	 * @param returnRequest
	 *           the request for which the manual refund has been done
	 */
	public boolean manualRefund(final ReturnRequestModel returnRequest)
	{
		if (returnRequest.getStatus().equals(ReturnStatus.COMPLETED))
		{
			LOG.error("Return request already success refund " + returnRequest);
			return false;
		}

		try
		{
			if (returnRequest.getStatus().equals(ReturnStatus.PAYMENT_FAILED))
			{
				getReturnBusinessProcessService().triggerChoiceEvent(returnRequest, FAIL_CAPTURE_ACTION_EVENT_NAME,
						BYPASS_CAPTURE_CHOICE);
			}
			else
			{
				getReturnBusinessProcessService().triggerChoiceEvent(returnRequest, CONFIRM_OR_CANCEL_REFUND_ACTION_EVENT_NAME,
						TAX_REVERSE_CHOICE);
			}
		}
		catch (final BusinessProcessException e)
		{
			LOG.error("manual refund fail", e);
			return false;
		}
		return true;
	}

	/**
	 * This event is triggered whenever there is an automatic refund done on a specific return request
	 *
	 * @param returnRequest
	 *           the request for which the automatic refund has been processed
	 */
	public void autoRefund(final ReturnRequestModel returnRequest)
	{
		try
		{
			getReturnBusinessProcessService().triggerChoiceEvent(returnRequest, CONFIRM_OR_CANCEL_REFUND_ACTION_EVENT_NAME,
					CAPTURE_REFUND_CHOICE);

			final Set<ReturnStatus> validStatus = new HashSet<>();
			validStatus.add(ReturnStatus.PAYMENT_CAPTURED);
			validStatus.add(ReturnStatus.PAYMENT_FAILED);
			validStatus.add(ReturnStatus.TAX_CAPTURED);
			validStatus.add(ReturnStatus.TAX_FAILED);
			validStatus.add(ReturnStatus.COMPLETED);

			ReturnStatus returnRequestStatus;
			ReturnRequestModel myReturnRequest;
			do
			{
				myReturnRequest = getModelService().get(returnRequest.getPk());
				returnRequestStatus = myReturnRequest.getStatus();
			}
			while (!validStatus.contains(returnRequestStatus));

			if (returnRequestStatus.equals(ReturnStatus.PAYMENT_FAILED))
			{
				LOG.error("auto refund fail");
			}
			else
			{
				LOG.error("auto refund success");
			}

		}
		catch (final BusinessProcessException e)
		{
			LOG.error("auto refund fail", e);
		}
	}

	/**
	 * This event is triggered whenever a refund is cancelled
	 *
	 * @param returnRequest
	 *           the request for which the automatic refund has been processed
	 */
	public void cancelRefund(final ReturnRequestModel returnRequest)
	{
		try
		{
			if (returnRequest.getStatus().equals(ReturnStatus.PAYMENT_FAILED))
			{
				getReturnBusinessProcessService().triggerChoiceEvent(returnRequest, FAIL_CAPTURE_ACTION_EVENT_NAME,
						CANCEL_REFUND_CHOICE);
			}
			else
			{
				getReturnBusinessProcessService().triggerChoiceEvent(returnRequest, CONFIRM_OR_CANCEL_REFUND_ACTION_EVENT_NAME,
						CANCEL_REFUND_CHOICE);
			}
		}
		catch (final BusinessProcessException e)
		{
			LOG.error("Cancel Refund fail", e);
		}
	}

	protected WarehousingBusinessProcessService<ReturnRequestModel> getReturnBusinessProcessService()
	{

		return returnBusinessProcessService;
	}

	public void setReturnBusinessProcessService(
			final WarehousingBusinessProcessService<ReturnRequestModel> returnBusinessProcessService)
	{
		this.returnBusinessProcessService = returnBusinessProcessService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
