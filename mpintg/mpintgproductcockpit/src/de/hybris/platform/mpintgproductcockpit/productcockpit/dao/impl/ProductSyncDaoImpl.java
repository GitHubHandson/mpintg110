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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.mpintgproductcockpit.productcockpit.dao.productSyncDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hybris.backoffice.model.MarketplaceStoreModel;


@Repository(value = "productSyncDao")
public class ProductSyncDaoImpl implements productSyncDao
{

	final String productsql = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} " + "WHERE {"
			+ ProductModel.CODE + "}=?productId";

	final String productPKsql = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} " + "WHERE {"
			+ ProductModel.PK + "}=?productPK";

	final String getProductByCatagroySql = "SELECT {" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} " + "WHERE {"
			+ ProductModel.CATALOGVERSION + "}=?catalogVersionPK";

	final String integrationsql = "SELECT {" + MarketplaceStoreModel.PK + "} FROM {" + MarketplaceStoreModel._TYPECODE + "} "
			+ "WHERE {" + MarketplaceStoreModel.BASESTORE + "}=?baseStorePK";

	@Autowired
	private FlexibleSearchService searchService;
	private final Logger LOG = Logger.getLogger(ProductSyncDaoImpl.class.getName());

	@Override
	public ProductModel getProductByPK(String productPK)
	{
		LOG.debug("getProductByPK start...");
		ServicesUtil.validateParameterNotNullStandardMessage("productPK", productPK);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("productPK", productPK);
		ProductModel product = null;
		final SearchResult<ProductModel> searchResult = searchService.search(productPKsql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			product = searchResult.getResult().get(0);
		}
		LOG.debug("getProductByPK finish...");
		return product;
	}

	@Override
	public ProductModel getProductById(String productId)
	{
		LOG.debug("getProductById start...");
		ServicesUtil.validateParameterNotNullStandardMessage("productId", productId);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("productId", productId);
		ProductModel product = null;
		final SearchResult<ProductModel> searchResult = searchService.search(productsql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			product = searchResult.getResult().get(0);
		}
		LOG.debug("getProductById finish...");
		return product;
	}

	@Override
	public String getIntegrationIdByBasestore(String baseStorePK)
	{
		LOG.debug("getIntegrationIdByBasestore start...");
		ServicesUtil.validateParameterNotNullStandardMessage("baseStorePK", baseStorePK);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("baseStorePK", baseStorePK);
		MarketplaceStoreModel store = null;
		final SearchResult<MarketplaceStoreModel> searchResult = searchService.search(integrationsql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			store = searchResult.getResult().get(0);
		}
		LOG.debug("getIntegrationIdByBasestore finish...");
		return store.getIntegrationId();
	}

	@Override
	public List<ProductModel> getProductsByCatalogVersion(String catalogVersionPK)
	{
		LOG.debug("getProductsByCatalogVersion start...");
		ServicesUtil.validateParameterNotNullStandardMessage("catalogVersionPK", catalogVersionPK);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("catalogVersionPK", catalogVersionPK);
		final SearchResult<ProductModel> searchResult = searchService.search(getProductByCatagroySql, params);
		LOG.debug("getProductsByCatalogVersion finish...");
		return searchResult.getResult();
	}

}
