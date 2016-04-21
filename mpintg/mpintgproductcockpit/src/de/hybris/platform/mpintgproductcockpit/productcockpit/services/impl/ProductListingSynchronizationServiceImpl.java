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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.sync.impl.SynchronizationServiceImpl;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.mpintgproductcockpit.productcockpit.services.ProductSyncService;
import de.hybris.platform.mpintgproductcockpit.productcockpit.util.MarketplaceintegrationHttpUtil;
import de.hybris.platform.servicelayer.type.TypeService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import com.ctc.wstx.util.StringUtil;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;


public class ProductListingSynchronizationServiceImpl extends SynchronizationServiceImpl
{

	@Value("${datahub.request.url}")
	private String requestUrl;

	public void setRequestUrl(String requestUrl)
	{
		this.requestUrl = requestUrl;
	}

	private static final Logger Log = Logger

	.getLogger(ProductListingSynchronizationServiceImpl.class);

	@Autowired
	MarketplaceintegrationHttpUtil marketplaceHttpUtil;

	@Autowired
	ProductSyncService productSyncService;

	TypeService platformTypeService;

	@Required
	public void setPlatformTypeService(TypeService typeService)
	{

		platformTypeService = typeService;

	}

	private static Map<String, Collection<AttributeDescriptorModel>> partOfADsForTypeCode = new ConcurrentHashMap<String, Collection<AttributeDescriptorModel>>();

	@Override
	public Collection<TypedObject> performSynchronization(Collection<? extends Object> items, List<String> syncJobPkList,
			CatalogVersionModel targetCatalogVersion, String qualifier)
	{
		Log.info("Product performSynchronization start... ");
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		System.out.println("Customization product logic ProductListingSynchronizationServiceImpl: " + format.format(date));
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");

		Collection<TypedObject> ret = super.performSynchronization(items, syncJobPkList, targetCatalogVersion, qualifier);

		this.productListing(items);

		return ret;
	}

	@Override
	public void performCatalogVersionSynchronization(Collection<CatalogVersionModel> paramCollection, List<String> paramList,
			CatalogVersionModel paramCatalogVersionModel, String paramString)
	{
		Log.info("Catagory performSynchronization start... ");

		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		System.out.println("Customization catelog logic ProductListingSynchronizationServiceImpl: " + format.format(date));
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		super.performCatalogVersionSynchronization(paramCollection, paramList, paramCatalogVersionModel, paramString);

		this.catagoryListing(paramCollection);

	}

	private void catagoryListing(Collection<CatalogVersionModel> paramCollection)
	{
		try
		{
			for (Object obj : paramCollection)
			{
				TypedObject wrappedItem = null;
				if (obj instanceof Item)
				{
					wrappedItem = getTypeService().wrapItem(((Item) obj).getPK());
				}
				else if (obj instanceof ItemModel)
				{
					wrappedItem = getTypeService().wrapItem(((ItemModel) obj).getPk());
				}
				else if (obj instanceof TypedObject)
				{
					wrappedItem = (TypedObject) obj;
				}
				List<ProductModel> products = productSyncService.getProductsByCatalogVersion(((ItemModel) wrappedItem.getObject())
						.getPk().toString());
				for (ProductModel product : products)
				{
					String produtctRequestUrl = "";
					if (!Strings.isNullOrEmpty(product.getTmallProductId()) && product.getListingStatus() != null)
					{
						final JsonObject jsonObj = new JsonObject();
						String marktplaceIntegrationId = productSyncService.getMarketplaceIntegrationIdbyProduct((product.getCode())
								.toString());
						if (StringUtil.equalEncodings("LISTING", product.getListingStatus().getCode()))
						{
							produtctRequestUrl = requestUrl + "tmall-product-list";
							jsonObj.addProperty("integrationId", marktplaceIntegrationId);
							jsonObj.addProperty("productId", product.getTmallProductId());
							jsonObj.addProperty("num", product.getQuantity());
						}
						else if (StringUtil.equalEncodings("DELISTING", product.getListingStatus().getCode()))
						{
							produtctRequestUrl = requestUrl + "tmall-product-delist";
							jsonObj.addProperty("integrationId", marktplaceIntegrationId);
							jsonObj.addProperty("productId", product.getTmallProductId());
						}
						Log.info("requestUrl is... " + requestUrl);
						final boolean result = marketplaceHttpUtil.post(produtctRequestUrl, jsonObj.toString());
						Log.info("Sync request result... " + result);
					}
				}

			}
		}
		catch (Exception e)
		{
			Log.info("Product performSynchronization error... " + e.toString());
		}
		Log.info("Product performSynchronization finish... ");
	}

	private void productListing(Collection<? extends Object> items)
	{
		try
		{
			for (Object obj : items)
			{
				String produtctRequestUrl = "";
				TypedObject wrappedItem = null;
				if (obj instanceof Item)
				{
					wrappedItem = getTypeService().wrapItem(((Item) obj).getPK());
				}
				else if (obj instanceof ItemModel)
				{
					wrappedItem = getTypeService().wrapItem(((ItemModel) obj).getPk());
				}
				else if (obj instanceof TypedObject)
				{
					wrappedItem = (TypedObject) obj;
				}

				ProductModel product = productSyncService.getProductById(((ProductModel) wrappedItem.getObject()).getCode()
						.toString());
				if (!Strings.isNullOrEmpty(product.getTmallProductId()))
				{
					final JsonObject jsonObj = new JsonObject();
					String marktplaceIntegrationId = productSyncService.getMarketplaceIntegrationIdbyProduct((product.getCode())
							.toString());
					if (StringUtil.equalEncodings("LISTING", product.getListingStatus().getCode()))
					{
						produtctRequestUrl = requestUrl + "tmall-product-list";
						jsonObj.addProperty("integrationId", marktplaceIntegrationId);
						jsonObj.addProperty("productId", product.getTmallProductId());
						jsonObj.addProperty("num", product.getQuantity());
					}
					else if (StringUtil.equalEncodings("DELISTING", product.getListingStatus().getCode()))
					{
						produtctRequestUrl = requestUrl + "tmall-product-delist";
						jsonObj.addProperty("integrationId", marktplaceIntegrationId);
						jsonObj.addProperty("productId", product.getTmallProductId());
					}
					else
					{
						produtctRequestUrl = requestUrl + "tmall-product-delist";
						jsonObj.addProperty("integrationId", marktplaceIntegrationId);
						jsonObj.addProperty("productId", product.getTmallProductId());
					}
					Log.info("requestUrl is... " + requestUrl);
					final boolean result = marketplaceHttpUtil.post(produtctRequestUrl, jsonObj.toString());
					Log.info("Sync request result... " + result);
				}

			}
		}
		catch (Exception e)
		{
			Log.info("Product performSynchronization error... " + e.toString());
		}
		Log.info("Product performSynchronization finish... ");
	}
}
