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
package de.hybris.platform.mpintgordermanagement.interceptor;

import com.hybris.backoffice.model.TmallRefundRequestModel;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import java.util.Set;



public class MarketplaceRefundPrepareInterceptor implements PrepareInterceptor<TmallRefundRequestModel>
{

	public MarketplaceRefundPrepareInterceptor()
	{
		// YTODO Auto-generated constructor stub
	}


	/*
	 * update product and product when model is new
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.PrepareInterceptor#onPrepare(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onPrepare(final TmallRefundRequestModel refundModel, final InterceptorContext context) throws InterceptorException
	{
		if (context.isNew(refundModel))
		{
			if (null == refundModel.getProduct())
			{
				// if product is null, set related product
				refundModel.setProduct(refundModel.getTmallOrderEntry().getProduct());
			}

			if (null == refundModel.getConsignment())
			{
				//if consignment is null, set related consignment
				final Set<ConsignmentEntryModel> consignmententries = refundModel.getTmallOrderEntry().getConsignmentEntries();
				for (final ConsignmentEntryModel consignmententry : consignmententries)
				{
					refundModel.setConsignment(consignmententry.getConsignment());
				}
			}

			if (null == refundModel.getReturnAddress())
			{
				//if return address is null, set default address
				final AddressModel defaultAddress = refundModel.getMarketplaceStore().getDefaultReturnAddress();
				refundModel.setReturnAddress(defaultAddress);
			}
		}
	}
}
