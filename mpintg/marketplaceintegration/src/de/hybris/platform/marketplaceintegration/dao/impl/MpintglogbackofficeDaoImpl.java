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
package de.hybris.platform.marketplaceintegration.dao.impl;

import de.hybris.platform.marketplaceintegration.dao.MpintglogbackofficeDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hybris.backoffice.model.MarketplaceLogModel;
import com.hybris.backoffice.model.MarketplaceStatusModel;
import com.hybris.backoffice.model.MessageSourceModel;


public class MpintglogbackofficeDaoImpl implements MpintglogbackofficeDao
{
	private static final Logger LOG = LoggerFactory.getLogger(MpintglogbackofficeDaoImpl.class);

	@Autowired
	private FlexibleSearchService searchService;

	final String marketpalceLogSqlByPK = "SELECT {" + MarketplaceLogModel.PK + "} FROM {" + MarketplaceLogModel._TYPECODE + "} "
			+ "WHERE {" + MarketplaceLogModel.PK + "}=?marketplaceLogPK";

	final String marketpalceLogSqlByCriteria = "SELECT {" + MarketplaceLogModel.PK + "} FROM {" + MarketplaceLogModel._TYPECODE
			+ "!} " + "WHERE {" + MarketplaceLogModel.OBJECTID + "}=?objectId" + " AND {" + MarketplaceLogModel.ACTION + "}=?action"
			+ " AND {" + MarketplaceLogModel.OBJECTTYPE + "}=?objectType";

	final String marketpalceActionStatusSql = "SELECT {" + MarketplaceStatusModel.PK + "} FROM {"
			+ MarketplaceStatusModel._TYPECODE + "} " + "WHERE {" + MarketplaceStatusModel.DIVISION + "}=?divisionCode" + " AND {"
			+ MarketplaceStatusModel.CODE + "}=?code";

	final String messageSourceSql = "SELECT {" + MessageSourceModel.PK + "} FROM {" + MessageSourceModel._TYPECODE + "} "
			+ "WHERE {" + MessageSourceModel.CODE + "}=?code";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.mpintglogbackoffice.dao.MpintglogbackofficeDao#save()
	 */
	@Override
	public String save()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.mpintglogbackoffice.dao.MpintglogbackofficeDao#doNavigate()
	 */
	@Override
	public void doNavigate()
	{
		// YTODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.mpintglogbackoffice.dao.MpintglogbackofficeDao#doAfterLog()
	 */
	@Override
	public void doAfterLog()
	{
		// YTODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.mpintglogbackoffice.dao.MpintglogbackofficeDao#getMarketplaceLog()
	 */
	@Override
	public MarketplaceLogModel getMarketplaceLog(final String marketplaceLogPK)
	{
		LOG.debug("getMarketplaceLog start...");
		ServicesUtil.validateParameterNotNullStandardMessage("marketplaceLogPK", marketplaceLogPK);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("marketplaceLogPK", marketplaceLogPK);
		MarketplaceLogModel log = null;
		final SearchResult<MarketplaceLogModel> searchResult = searchService.search(marketpalceLogSqlByPK, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			log = searchResult.getResult().get(0);
		}
		LOG.debug("getMarketplaceLog finish...");
		return log;
	}

	@Override
	public MarketplaceStatusModel getActionStatus(final String divisionCode, final String code)
	{
		LOG.debug("getActionStatus start...");
		ServicesUtil.validateParameterNotNullStandardMessage("divisionCode", divisionCode);
		ServicesUtil.validateParameterNotNullStandardMessage("code", code);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("divisionCode", divisionCode);
		params.put("code", code);
		MarketplaceStatusModel status = null;
		final SearchResult<MarketplaceStatusModel> searchResult = searchService.search(marketpalceActionStatusSql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			status = searchResult.getResult().get(0);
		}
		LOG.debug("getActionStatus end...");
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.marketplaceintegrationbackoffice.dao.MpintglogbackofficeDao#getMarketplaceLog(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public MarketplaceLogModel getMarketplaceLog(final String objectId, final String action, final String objectType,
			final String actionStatus)
	{
		LOG.debug("getMarketplaceLog start...");
		ServicesUtil.validateParameterNotNullStandardMessage("objectId", objectId);
		ServicesUtil.validateParameterNotNullStandardMessage("action", action);
		ServicesUtil.validateParameterNotNullStandardMessage("objectType", objectType);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("objectId", objectId);
		params.put("action", action);
		params.put("objectType", objectType);
		MarketplaceLogModel log = null;

		final SearchResult<MarketplaceLogModel> searchResult = searchService.search(marketpalceLogSqlByCriteria, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			log = searchResult.getResult().get(0);
		}
		LOG.debug("getMarketplaceLog finish...");
		return log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.marketplaceintegration.dao.MpintglogbackofficeDao#getMessageSource(java.lang.String)
	 */
	@Override
	public MessageSourceModel getMessageSource(final String sourceCode)
	{
		LOG.debug("getMessageSource start...");
		ServicesUtil.validateParameterNotNullStandardMessage("code", sourceCode);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("code", sourceCode);
		MessageSourceModel source = null;
		final SearchResult<MessageSourceModel> searchResult = searchService.search(messageSourceSql, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			source = searchResult.getResult().get(0);
		}
		LOG.debug("getMessageSource end...");
		return source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.marketplaceintegration.dao.MpintglogbackofficeDao#getMarketplaceLog(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public MarketplaceLogModel getMarketplaceLog(final String objectId, final String action, final String objectType)
	{
		LOG.debug("getMarketplaceLog start...");
		ServicesUtil.validateParameterNotNullStandardMessage("objectId", objectId);
		ServicesUtil.validateParameterNotNullStandardMessage("action", action);
		ServicesUtil.validateParameterNotNullStandardMessage("objectType", objectType);

		final Map<String, String> params = new HashMap<String, String>();
		params.put("objectId", objectId);
		params.put("action", action);
		params.put("objectType", objectType);
		MarketplaceLogModel log = null;

		final SearchResult<MarketplaceLogModel> searchResult = searchService.search(marketpalceLogSqlByCriteria, params);
		if (CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			log = searchResult.getResult().get(0);
		}
		LOG.debug("getMarketplaceLog finish...");
		return log;
	}
}
