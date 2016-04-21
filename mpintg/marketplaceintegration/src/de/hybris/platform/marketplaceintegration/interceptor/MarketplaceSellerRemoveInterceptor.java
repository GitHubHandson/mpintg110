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

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.backoffice.model.MarketplaceSellerModel;



public class MarketplaceSellerRemoveInterceptor implements RemoveInterceptor
{
	private static final Logger LOG = LoggerFactory.getLogger(MarketplaceSellerRemoveInterceptor.class);

	@Resource
	protected FlexibleSearchService flexibleSearchService;

	public MarketplaceSellerRemoveInterceptor()
	{
		// YTODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.RemoveInterceptor#onRemove(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onRemove(final Object paramMODEL, final InterceptorContext paramInterceptorContext) throws InterceptorException
	{
		if (paramMODEL instanceof MarketplaceSellerModel)
		{
			final MarketplaceSellerModel seller = (MarketplaceSellerModel) paramMODEL;
			final PK sellerPK = seller.getPk();
			final FlexibleSearchQuery fsq = new FlexibleSearchQuery(
					"select * from {MarketplaceStore} where {marketplaceSeller} = ?sellerPK", Collections.singletonMap("sellerPK",
							sellerPK));
			if (0 < flexibleSearchService.search(fsq).getTotalCount())
			{
				LOG.info("object has been referred,can not be removed!");
				throw new InterceptorException("object has been referred,can not be removed!");
			}
		}

	}

}
