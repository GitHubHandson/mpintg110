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
package de.hybris.platform.marketplaceintegration.interceptor;

import de.hybris.platform.marketplaceintegration.model.MarketplaceOrderModel;
import de.hybris.platform.marketplaceintegration.model.TmallOrderModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;

import org.springframework.beans.factory.annotation.Required;


public class MarketplaceOrderPrepareInterceptor implements PrepareInterceptor<MarketplaceOrderModel>
{
	private ModelService modelService;

	public MarketplaceOrderPrepareInterceptor()
	{
		// YTODO Auto-generated constructor stub
	}


	/*
	 * update store and seller when model is new or modified
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.PrepareInterceptor#onPrepare(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onPrepare(final MarketplaceOrderModel orderModel, final InterceptorContext context) throws InterceptorException
	{
		if (context.isModified(orderModel) || context.isNew(orderModel))
		{
			if (null == orderModel.getSeller() && null != orderModel.getMarketplaceStore())
			{
				// if store is not null, then set seller the store's related seller
				orderModel.setSeller(orderModel.getMarketplaceStore().getMarketplaceSeller());
			}
			else if (null != orderModel.getSeller() && null == orderModel.getMarketplaceStore())
			{
				//if store delete then delete related seller
				orderModel.setSeller(null);
			}
		}

		if (orderModel instanceof TmallOrderModel)
		{
			final TmallOrderModel tmallOrderModel = (TmallOrderModel) orderModel;
			handleOrderStatus(tmallOrderModel, context);
		}
	}

	public void handleOrderStatus(final TmallOrderModel tmallOrderModel, final InterceptorContext context)
	{
		// Datahub do not transfer order status any more, we get it from corresponding tmall order status
		if (context.isNew(tmallOrderModel) || "TRADE_CLOSED_BY_TAOBAO".equals(tmallOrderModel.getTmallOrderStatus().getCode()))
		{
			tmallOrderModel.setStatus(tmallOrderModel.getTmallOrderStatus().getOrderStatus());
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
