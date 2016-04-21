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

import de.hybris.platform.marketplaceintegration.service.MarketplaceintegrationService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import javax.annotation.Resource;

import com.hybris.backoffice.model.MarketplaceModel;


public class MarketplacePrepareInterceptor implements PrepareInterceptor
{
	@Resource
	private MarketplaceintegrationService marketplaceintegrationService;

	public MarketplacePrepareInterceptor()
	{
		// YTODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.RemoveInterceptor#onRemove(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onPrepare(final Object paramMODEL, final InterceptorContext paramInterceptorContext) throws InterceptorException
	{
		if (paramMODEL instanceof MarketplaceModel)
		{
			final MarketplaceModel marketplace = (MarketplaceModel) paramMODEL;
			if (paramInterceptorContext.isNew(marketplace))
			{
				if (marketplaceintegrationService.getMarketplaceByCode(marketplace.getCode()) != null)
				{
					throw new InterceptorException("Marketplace code [" + marketplace.getCode() + "] is existing!");
				}
			}
			else if (paramInterceptorContext.isModified(marketplace))
			{
				if (paramInterceptorContext.isModified(marketplace, MarketplaceModel.CODE))
				{
					if (marketplaceintegrationService.getMarketplaceByCode(marketplace.getCode()) != null)
					{
						throw new InterceptorException("Marketplace code [" + marketplace.getCode() + "] is existing!");
					}
				}
				if (paramInterceptorContext.isModified(marketplace, MarketplaceModel.TMALLORDERSTATUS))
				{
					if (marketplace.getTmallOrderStatus() == null || marketplace.getTmallOrderStatus().size() < 1)
					{
						throw new InterceptorException("Please maintain Ext. Order Status.");
					}
				}
			}
		}
	}
}
