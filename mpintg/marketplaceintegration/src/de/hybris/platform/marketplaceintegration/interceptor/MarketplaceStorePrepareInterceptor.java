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

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import javax.annotation.Resource;

import com.hybris.backoffice.model.MarketplaceStoreModel;




public class MarketplaceStorePrepareInterceptor implements PrepareInterceptor
{
	@Resource
	protected FlexibleSearchService flexibleSearchService;

	private static CurrencyModel currency = null;


	public MarketplaceStorePrepareInterceptor()
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
		if (paramMODEL instanceof MarketplaceStoreModel)
		{
			final MarketplaceStoreModel store = (MarketplaceStoreModel) paramMODEL;
			if (null != store.getMarketplaceSeller())
			{
				store.setMarketplace(store.getMarketplaceSeller().getMarketplace());
				store.setBaseStore(store.getMarketplaceSeller().getBaseStore());
			}

			if (null == currency)
			{
				final FlexibleSearchQuery fsq = new FlexibleSearchQuery("select {PK} from {Currency} where {isocode}='CNY'");
				final SearchResult<CurrencyModel> result = flexibleSearchService.search(fsq);
				if (null == result.getResult() || result.getResult().isEmpty())
				{
					throw new InterceptorException("Currency is empty!");
				}
				currency = result.getResult().get(0);
			}
			store.setCurrency(currency);
		}
	}
}
