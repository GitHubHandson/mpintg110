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
package de.hybris.platform.mpintgproductcockpit.productcockpit.services;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import org.springframework.stereotype.Component;


@Component
public interface ProductSyncService
{

	BaseStoreModel getBaseStorebyProduct(String productId);

	BaseStoreModel getBaseStorebyPK(String productPK);

	String getMarketplaceIntegrationIdbyProduct(String productId);

	ProductModel getProductByPK(String productPK);

	ProductModel getProductById(String productCode);

	List<ProductModel> getProductsByCatalogVersion(String catalogVersionPK);
}
