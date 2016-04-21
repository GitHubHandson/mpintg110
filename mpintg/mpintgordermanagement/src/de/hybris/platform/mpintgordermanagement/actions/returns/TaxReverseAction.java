/*
 *  
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
 */
package de.hybris.platform.mpintgordermanagement.actions.returns;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.util.Config;
import de.hybris.platform.warehousing.model.ReturnProcessModel;

import org.apache.log4j.Logger;


/**
 * Reverse tax calculation and update the {@link ReturnRequestModel} status to TAX_FAILED or TAX_CAPTURED.
 */
public class TaxReverseAction extends AbstractSimpleDecisionAction<ReturnProcessModel>
{
	private static final Logger LOG = Logger.getLogger(TaxReverseAction.class);
	public static final String TAX_REVERSE_FORCE_FAILURE = "mpintgordermanagement.reverse.tax.force.failure";

	@Override
	public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException, Exception
	{
		LOG.debug("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final ReturnRequestModel returnRequest = process.getReturnRequest();

		// TODO: implement tax reverse

		final boolean testFailCapture = Config.getBoolean(TAX_REVERSE_FORCE_FAILURE, false);
		if (testFailCapture)
		{
			returnRequest.setStatus(ReturnStatus.TAX_FAILED);
			return Transition.NOK;
		}
		else
		{
			returnRequest.setStatus(ReturnStatus.TAX_CAPTURED);
			return Transition.OK;
		}
	}
}
