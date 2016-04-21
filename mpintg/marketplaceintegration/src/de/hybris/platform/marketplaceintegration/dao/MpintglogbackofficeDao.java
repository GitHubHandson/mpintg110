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
 *
 *
 */
package de.hybris.platform.marketplaceintegration.dao;

import org.springframework.stereotype.Repository;

import com.hybris.backoffice.model.MarketplaceLogModel;
import com.hybris.backoffice.model.MarketplaceStatusModel;
import com.hybris.backoffice.model.MessageSourceModel;


@Repository
public interface MpintglogbackofficeDao
{
	public String save();

	public void doNavigate();

	public void doAfterLog();

	public MarketplaceStatusModel getActionStatus(String statusCode, String divisionCode);

	public MessageSourceModel getMessageSource(String sourceCode);

	public MarketplaceLogModel getMarketplaceLog(final String marketplaceLogPK);

	public MarketplaceLogModel getMarketplaceLog(final String objectId, final String action, final String objectType,
			final String actionStatus);

	public MarketplaceLogModel getMarketplaceLog(final String objectId, final String action, final String objectType);

}
