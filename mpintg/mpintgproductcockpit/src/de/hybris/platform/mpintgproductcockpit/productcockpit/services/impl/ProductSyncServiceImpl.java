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
package de.hybris.platform.mpintgproductcockpit.productcockpit.services.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.mpintgproductcockpit.productcockpit.dao.impl.ProductSyncDaoImpl;
import de.hybris.platform.mpintgproductcockpit.productcockpit.services.ProductSyncService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductSyncServiceImpl implements ProductSyncService
{

	@Resource
	@Autowired
	ProductSyncDaoImpl productSyncDao;


	@Override
	public BaseStoreModel getBaseStorebyPK(String productPK)
	{
		BaseStoreModel baseStore = null;
		ProductModel product = productSyncDao.getProductByPK(productPK);
		if (product != null)
		{
			if (product.getCatalogVersion().getCatalog().getBaseStores().iterator().hasNext())
			{
				baseStore = product.getCatalogVersion().getCatalog().getBaseStores().iterator().next();
			}
		}
		return baseStore;
	}

	@Override
	public String getMarketplaceIntegrationIdbyProduct(String productId)
	{
		ProductModel product = productSyncDao.getProductById(productId);
		BaseStoreModel baseStore = product.getCatalogVersion().getCatalog().getBaseStores().iterator().next();
		String id = productSyncDao.getIntegrationIdByBasestore(baseStore.getPk().toString());
		return id;
	}

	@Override
	public BaseStoreModel getBaseStorebyProduct(String productId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductModel getProductByPK(String productPK)
	{
		return productSyncDao.getProductByPK(productPK);
	}

	@Override
	public List<ProductModel> getProductsByCatalogVersion(String catalogVersionPK)
	{
		return productSyncDao.getProductsByCatalogVersion(catalogVersionPK);
	}

	@Override
	public ProductModel getProductById(String productId)
	{
		return productSyncDao.getProductById(productId);
	}
}
