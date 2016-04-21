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
package de.hybris.platform.mpintgproductcockpit.productcockpit.dao;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;


public interface productStockDao
{
	ProductModel getProductById(String productId);

	ProductModel getProductByPK(String productPK);

	ProductModel getProductbyPK(String PK);

	StockLevelModel getStockLevel(PK warehouse, String productId);

	String getIntegrationIdByBasestore(String baseStorePK);
}
