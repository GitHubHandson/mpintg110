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
package de.hybris.platform.marketplaceintegration.dao.impl;

import de.hybris.platform.marketplaceintegration.dao.MarketplaceintegrationDao;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hybris.backoffice.model.MarketplaceModel;
import com.hybris.backoffice.model.MarketplaceStoreModel;


/**
 * Default implementation for {@link MarketplaceintegrationDao}
 */
public class MarketplaceintegrationDaoImpl extends AbstractItemDao implements MarketplaceintegrationDao
{

	private final String getMarketplaceByPK = "SELECT {mp.PK} FROM {Marketplace as mp} WHERE {mp.PK} = ?PK ";

	private final String getMarketplaceByCode = "SELECT {mp.PK} FROM {Marketplace as mp} WHERE {mp.CODE} = ?CODE ";

	private final String getMarketplaceStroeByPK = "SELECT {store.PK} FROM {MarketplaceStore as store} WHERE {store.PK} = ?PK ";

	private final String getMarketplaceStroeByMarketplaceUrlSql = "SELECT {store.PK} FROM {MarketplaceStore as store} WHERE {store.marketplace} IN "
			+ "({{ SELECT {PK} FROM {Marketplace} WHERE P_URL = ?URL }})";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.marketplaceintegration.dao.MarketplaceintegrationDao#getMarketplaceStores(java.lang.String)
	 */
	@Override
	public List<MarketplaceStoreModel> getMarketplaceStoresByMarketplaceUrl(final String marketplaceUrl)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("URL", marketplaceUrl);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(getMarketplaceStroeByMarketplaceUrlSql);
		query.addQueryParameters(params);
		query.setResultClassList(Arrays.asList(MarketplaceStoreModel.class));

		final SearchResult<MarketplaceStoreModel> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.marketplaceintegration.dao.MarketplaceintegrationDao#getMarketplaceByPK(java.lang.String)
	 */
	@Override
	public MarketplaceModel getMarketplaceByPK(final String PK)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("PK", PK);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(getMarketplaceByPK);
		query.addQueryParameters(params);
		query.setResultClassList(Arrays.asList(MarketplaceModel.class));

		final SearchResult<MarketplaceModel> result = getFlexibleSearchService().search(query);
		return result.getResult().get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.marketplaceintegration.dao.MarketplaceintegrationDao#getMarketplaceStoreByPK(java.lang.String)
	 */
	@Override
	public MarketplaceStoreModel getMarketplaceStoreByPK(final String PK)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("PK", PK);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(getMarketplaceStroeByPK);
		query.addQueryParameters(params);
		query.setResultClassList(Arrays.asList(MarketplaceStoreModel.class));

		final SearchResult<MarketplaceStoreModel> result = getFlexibleSearchService().search(query);
		return result.getResult().get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.marketplaceintegration.dao.MarketplaceintegrationDao#getMarketplaceStoreByCode(java.lang.String
	 * )
	 */
	@Override
	public MarketplaceModel getMarketplaceByCode(final String marketplaceCode)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("CODE", marketplaceCode);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(getMarketplaceByCode);
		query.addQueryParameters(params);
		query.setResultClassList(Arrays.asList(MarketplaceModel.class));

		final SearchResult<MarketplaceModel> result = getFlexibleSearchService().search(query);
		if (result.getTotalCount() > 0)
		{
			return result.getResult().get(0);
		}
		else
		{
			return null;
		}
	}
}
