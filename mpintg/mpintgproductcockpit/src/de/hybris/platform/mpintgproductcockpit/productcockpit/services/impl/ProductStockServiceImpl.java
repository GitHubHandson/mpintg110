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
import de.hybris.platform.mpintgproductcockpit.cmscockpit.dao.impl.ProductStockDaoImpl;
import de.hybris.platform.mpintgproductcockpit.cmscockpit.services.ProductStockService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class ProductStockServiceImpl implements ProductStockService
{

	public ProductStockDaoImpl getProductStockDao()
	{
		return productStockDao;
	}

	@Required
	public void setProductStockDao(ProductStockDaoImpl productStockDao)
	{
		this.productStockDao = productStockDao;
	}

	ProductStockDaoImpl productStockDao;

	@Override
	public int getAvailableStockbyPK(BaseStoreModel basestore, String productPK)
	{
		List<WarehouseModel> warehouses = basestore.getWarehouses();
		int allAvailableStock = 0;
		for (int i = 0; i < warehouses.size(); i++)
		{
			ProductModel product = productStockDao.getProductbyPK(productPK);
			String productId = product.getCode();
			StockLevelModel stocklevel = productStockDao.getStockLevel(warehouses.get(i).getPk(), productId);
			if (stocklevel != null)
			{
				allAvailableStock += stocklevel.getAvailable();
			}
		}
		return allAvailableStock;
	}

	@Override
	public BaseStoreModel getBaseStorebyPK(String productPK)
	{
		BaseStoreModel baseStore = null;
		ProductModel product = productStockDao.getProductbyPK(productPK);
		if (product != null)
		{
			if (product.getCatalogVersion().getCatalog().getBaseStores().iterator().hasNext())
			{
				baseStore = product.getCatalogVersion().getCatalog().getBaseStores().iterator().next();
			}
		}
		return baseStore;
	}

	/*
	 * public String getMarketplaceIntegrationIdbyProduct(String productId) { ProductModel product =
	 * productStockDao.getProductById(productId); //ProductModel product = productStockDao.getProductByPk(productId);
	 * BaseStoreModel baseStore = product.getCatalogVersion().getCatalog().getBaseStores().iterator().next(); String id =
	 * productStockDao.getIntegrationIdByBasestore(baseStore.getPk().toString()); return id; }
	 */

	/*
	 * public ProductModel getProductByPK(String productPK) { return productStockDao.getProductByPK(productPK); }
	 */

	@Override
	public int getAvailableStock(BaseStoreModel basestore, String productId)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BaseStoreModel getBaseStorebyProduct(String productId)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
