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
package de.hybris.platform.marketplaceintegration.service.impl;

import de.hybris.platform.marketplaceintegration.dao.MarketplaceintegrationDao;
import de.hybris.platform.marketplaceintegration.service.MarketplaceintegrationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hybris.backoffice.model.MarketplaceModel;
import com.hybris.backoffice.model.MarketplaceStoreModel;


/**
 * Default implementation for {@link MarketplaceintegrationService}
 */
@Service
public class MarketplaceintegrationServiceImpl implements MarketplaceintegrationService
{

	@Resource
	private ModelService modelService;
	@Resource
	private MarketplaceintegrationDao marketplaceintegrationDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.marketplaceintegration.service.MarketplaceintegrationService#getMarketplace(java.lang.String)
	 */
	@Override
	public MarketplaceModel getMarketplaceByPK(final String PK)
	{
		return marketplaceintegrationDao.getMarketplaceByPK(PK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.marketplaceintegration.service.MarketplaceintegrationService#getMarketplaceStore(java.lang.
	 * String)
	 */
	@Override
	public MarketplaceStoreModel getMarketplaceStoreByPK(final String PK)
	{
		return marketplaceintegrationDao.getMarketplaceStoreByPK(PK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.marketplaceintegration.service.MarketplaceintegrationService#getMarketplaceStores(java.lang
	 * .String)
	 */
	@Override
	public List<MarketplaceStoreModel> getMarketplaceStoresByMarketplaceUrl(final String marketplaceUrl)
	{
		return marketplaceintegrationDao.getMarketplaceStoresByMarketplaceUrl(marketplaceUrl);
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the marketplaceintegrationDao
	 */
	public MarketplaceintegrationDao getMarketplaceintegrationDao()
	{
		return marketplaceintegrationDao;
	}

	/**
	 * @param marketplaceintegrationDao
	 *           the marketplaceintegrationDao to set
	 */
	public void setMarketplaceintegrationDao(final MarketplaceintegrationDao marketplaceintegrationDao)
	{
		this.marketplaceintegrationDao = marketplaceintegrationDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.marketplaceintegration.service.MarketplaceintegrationService#getMarketplaceStoreByCode(java
	 * .lang.String)
	 */
	@Override
	public MarketplaceModel getMarketplaceByCode(final String marketplaceCode)
	{
		return marketplaceintegrationDao.getMarketplaceByCode(marketplaceCode);
	}
}
