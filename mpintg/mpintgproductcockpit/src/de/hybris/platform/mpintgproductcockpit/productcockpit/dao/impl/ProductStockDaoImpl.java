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
package de.hybris.platform.mpintgproductcockpit.productcockpit.dao.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.mpintgproductcockpit.cmscockpit.dao.productStockDao;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hybris.backoffice.model.MarketplaceStoreModel;


public class ProductStockDaoImpl implements productStockDao
{
	final String productsql = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} " + "WHERE {"
			+ ProductModel.CODE + "}=?productId";

	final String productPKsql = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} " + "WHERE {"
			+ ProductModel.PK + "}=?productPK";

	final String getProductByPKsql = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} " + "WHERE {"
			+ ProductModel.PK + "}=?productPK";

	final String stocksql = "SELECT {" + StockLevelModel.PK + "} FROM {" + StockLevelModel._TYPECODE + "} " + "WHERE {"
			+ StockLevelModel.WAREHOUSE + "}=?warehouse" + " AND {" + StockLevelModel.PRODUCTCODE + "}=?productId";

	final String integrationsql = "SELECT {" + MarketplaceStoreModel.PK + "} FROM {" + MarketplaceStoreModel._TYPECODE + "} "
			+ "WHERE {" + MarketplaceStoreModel.BASESTORE + "}=?baseStorePK";

	@Autowired
	private FlexibleSearchService searchService;
	private final Logger LOG = Logger.getLogger(ProductStockDaoImpl.class.getName());


	public ProductModel getProductById(String productId)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("productId", productId);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("productId", productId);
		ProductModel product = null;
		final SearchResult<ProductModel> searchResult = searchService.search(productsql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			product = searchResult.getResult().get(0);
		}
		return product;
	}

	@Override
	public ProductModel getProductbyPK(String productPK)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("productPK", productPK);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("productPK", productPK);
		ProductModel product = null;
		final SearchResult<ProductModel> searchResult = searchService.search(productPKsql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			product = searchResult.getResult().get(0);
		}
		return product;
	}

	@Override
	public StockLevelModel getStockLevel(PK pk, String productId)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("productId", productId);
		ServicesUtil.validateParameterNotNullStandardMessage("warehouse", pk);
		final Map<String, String> params = new HashMap<String, String>();
		params.put("productID", productId);
		params.put("warehouse", pk.toString());

		StockLevelModel StockLevel = null;
		final SearchResult<StockLevelModel> searchResult = searchService.search(stocksql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			StockLevel = searchResult.getResult().get(0);
		}
		return StockLevel;
	}

	public String getIntegrationIdByBasestore(String baseStorePK)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("baseStorePK", baseStorePK);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("baseStorePK", baseStorePK);
		MarketplaceStoreModel store = null;
		final SearchResult<MarketplaceStoreModel> searchResult = searchService.search(integrationsql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			store = searchResult.getResult().get(0);
		}
		return store.getIntegrationId();
	}

	public ProductModel getProductByPK(String productPk)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("productPk", productPk);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("productPK", productPk);
		ProductModel product = null;
		final SearchResult<ProductModel> searchResult = searchService.search(getProductByPKsql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			product = searchResult.getResult().get(0);
		}
		return product;
	}

	@Override
	public ProductModel getProduct(String productId)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
