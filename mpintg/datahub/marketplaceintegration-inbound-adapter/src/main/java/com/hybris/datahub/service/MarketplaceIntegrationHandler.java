/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.datahub.service;


/**
 * MarketplaceIntegrationHandler
 */
public interface MarketplaceIntegrationHandler
{
	/**
	 * @param context
	 * @return result
	 */
	int handle(String context);
}
