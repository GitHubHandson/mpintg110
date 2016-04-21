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
package de.hybris.platform.marketplaceintegration.service;

import java.util.List;

import com.hybris.backoffice.model.MarketplaceModel;
import com.hybris.backoffice.model.MarketplaceStoreModel;


/**
 * Service providing Marketplace integration oriented functionality.
 */
public interface MarketplaceintegrationService
{
	public MarketplaceModel getMarketplaceByPK(String PK);

	public MarketplaceModel getMarketplaceByCode(String marketplaceCode);

	public MarketplaceStoreModel getMarketplaceStoreByPK(String PK);

	public List<MarketplaceStoreModel> getMarketplaceStoresByMarketplaceUrl(String marketplaceUrl);
}
