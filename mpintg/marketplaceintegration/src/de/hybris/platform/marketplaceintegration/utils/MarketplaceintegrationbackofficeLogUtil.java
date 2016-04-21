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
package de.hybris.platform.marketplaceintegration.utils;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.marketplaceintegration.dao.MpintglogbackofficeDao;
import de.hybris.platform.marketplaceintegration.model.TmallOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hybris.backoffice.model.MarketplaceLogHistoryModel;
import com.hybris.backoffice.model.MarketplaceLogModel;
import com.hybris.backoffice.model.MarketplaceModel;
import com.hybris.backoffice.model.MarketplaceStatusModel;
import com.hybris.backoffice.model.MessageSourceModel;


/**
 * MarketplaceintegrationbackofficeLogUtilImpl
 */
@Component("MarketplaceintegrationbackofficeLogUtil")
public class MarketplaceintegrationbackofficeLogUtil
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MarketplaceintegrationbackofficeLogUtil.class);
	private static final String ACTIONSTATUS = "ACTIONSTATUS";
	private static final String BKCKOFFICE = "BK";

	@Resource(name = "mpintglogbackofficeDao")
	private MpintglogbackofficeDao mpintglogbackofficeDao;

	@Autowired
	private ModelService modelService;

	@Autowired
	private UserService userService;

	public MarketplaceintegrationbackofficeLogUtil()
	{
		//initializationTemplate();
	}

	public String getUUID()
	{
		final UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}


	public MarketplaceStatusModel getMappingStatus(final String itemType, final String statusCode)
	{
		return mpintglogbackofficeDao.getActionStatus(itemType, statusCode);
	}


	public MessageSourceModel getMessageSource(final String sourceCode)
	{
		return mpintglogbackofficeDao.getMessageSource(sourceCode);
	}


	public boolean saveMarketplaceLog(final String itemType, final String statusCode)
	{
		return false;
	}


	public void addMarketplaceLog(final String actionStatus, final String objectId, final String action, final String objectType,
			final MarketplaceModel marketplaceModel, final ItemModel targetObject, final String logUUID)
	{
		LOGGER.info("addMarketplaceLog start... ");

		final MarketplaceLogHistoryModel logHistory = modelService.create(MarketplaceLogHistoryModel._TYPECODE);
		logHistory.setActionstatus(this.getMappingStatus(ACTIONSTATUS, actionStatus));
		logHistory.setMarketplace(marketplaceModel);
		logHistory.setObjectid(objectId);
		logHistory.setActionuuid(logUUID);
		logHistory.setSubactionuuid(logUUID);
		logHistory.setAction(action);
		logHistory.setObjecttype(objectType);
		logHistory.setMessagesource(this.getMessageSource(BKCKOFFICE));
		logHistory.setTargetobject(targetObject);
		logHistory.setOwner(userService.getCurrentUser());
		modelService.save(logHistory);
		LOGGER.info("ACTION " + action + " logUUID:  " + logUUID);
		LOGGER.info("addMarketplaceLog finish... ");
	}

	public void addMarketplaceOrderLog(final String actionStatus, final String objectId, final String action,
			final TmallOrderModel targetObject, final String logUUID, final String message)
	{
		LOGGER.info("addMarketplaceLog start... ");
		final MarketplaceLogHistoryModel logHistory = modelService.create(MarketplaceLogHistoryModel._TYPECODE);
		logHistory.setActionstatus(this.getMappingStatus(ACTIONSTATUS, actionStatus));
		logHistory.setMarketplace(targetObject.getMarketplaceStore().getMarketplace());
		logHistory.setObjectid(targetObject.getCode());
		logHistory.setSubobjectid(objectId);
		logHistory.setActionuuid(logUUID);
		logHistory.setSubactionuuid(logUUID);
		logHistory.setAction(action);
		logHistory.setObjecttype(targetObject.getItemtype());
		logHistory.setMessage(message);
		logHistory.setMessagesource(this.getMessageSource(BKCKOFFICE));
		logHistory.setTargetobject(targetObject);
		logHistory.setOwner(userService.getCurrentUser());
		modelService.save(logHistory);
		LOGGER.info("ACTION " + action + " logUUID:  " + logUUID);
		LOGGER.info("addMarketplaceLog finish... ");
	}

	public MarketplaceLogModel getMarketplaceLog(final String objectId, final String action, final String objectType)
	{
		return mpintglogbackofficeDao.getMarketplaceLog(objectId, action, objectType);
	}
}
