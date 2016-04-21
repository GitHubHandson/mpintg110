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
package de.hybris.platform.mpintgproductcockpit.productcockpit.util;

import java.io.IOException;


/**
 *
 */
public interface MarketplaceintegrationHttpUtil
{
	/**
	 * Post data
	 *
	 * @param url
	 * @param data
	 * @return boolean
	 * @throws IOException
	 */
	public boolean post(final String url, final String data) throws IOException;

}
