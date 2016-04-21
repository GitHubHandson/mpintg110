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
package de.hybris.platform.marketplaceintegration.interceptor;

import de.hybris.platform.marketplaceintegration.service.MarketplaceintegrationService;
import de.hybris.platform.marketplaceintegration.utils.MarketplaceintegrationbackofficeLogUtil;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.backoffice.model.MarketplaceLogHistoryModel;
import com.hybris.backoffice.model.MarketplaceLogModel;


public class MarketplaceLogPrepareInterceptor implements PrepareInterceptor
{

	private ModelService modelService;
	@Resource
	private MarketplaceintegrationService marketplaceintegrationService;
	@Autowired
	private MarketplaceintegrationbackofficeLogUtil logUtil;

	public MarketplaceLogPrepareInterceptor()
	{
		// YTODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.RemoveInterceptor#onRemove(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onPrepare(final Object paramMODEL, final InterceptorContext paramInterceptorContext) throws InterceptorException
	{
		if (paramMODEL instanceof MarketplaceLogHistoryModel)
		{
			final MarketplaceLogHistoryModel logHis = (MarketplaceLogHistoryModel) paramMODEL;
			if (paramInterceptorContext.isNew(logHis))
			{

				final MarketplaceLogModel marketplaceLog = logUtil.getMarketplaceLog(logHis.getObjectid(), logHis.getAction(),
						logHis.getObjecttype());
				if (marketplaceLog != null && StringUtils.isNotEmpty(marketplaceLog.getPk().toString()))
				{
					this.updateMarketplaceLog(marketplaceLog, logHis);
					logHis.setActionuuid(marketplaceLog.getActionuuid());
					logHis.setMarketplacelog(marketplaceLog);
				}
				else
				{
					logHis.setMarketplacelog(this.saveMarketplaceLog(logHis));
				}

			}
			else if (paramInterceptorContext.isModified(logHis))
			{
				if (paramInterceptorContext.isModified(logHis, MarketplaceLogHistoryModel.ACTIONSTATUS))
				{
					final MarketplaceLogModel marketplaceLog = logHis.getMarketplacelog();
					if (marketplaceLog != null)
					{
						this.updateMarketplaceLog(marketplaceLog, logHis);
					}
				}
			}
		}

	}

	private MarketplaceLogModel saveMarketplaceLog(final MarketplaceLogHistoryModel logHis)
	{
		final MarketplaceLogModel marketplaceLog = modelService.create(MarketplaceLogModel._TYPECODE);
		marketplaceLog.setActionstatus(logHis.getActionstatus());
		marketplaceLog.setMarketplace(logHis.getMarketplace());
		marketplaceLog.setSubobjectid(logHis.getSubobjectid());
		marketplaceLog.setObjectid(logHis.getObjectid());
		marketplaceLog.setActionuuid(logHis.getActionuuid());
		marketplaceLog.setAction(logHis.getAction());
		marketplaceLog.setObjecttype(logHis.getObjecttype());
		marketplaceLog.setMessage(logHis.getMessage());
		marketplaceLog.setMessagesource(logHis.getMessagesource());
		marketplaceLog.setTargetobject(logHis.getTargetobject());
		marketplaceLog.setOwner(logHis.getOwner());
		modelService.save(marketplaceLog);
		return marketplaceLog;
	}

	private void updateMarketplaceLog(final MarketplaceLogModel marketplaceLog, final MarketplaceLogHistoryModel logHis)
	{
		marketplaceLog.setActionuuid(logHis.getActionuuid());
		marketplaceLog.setActionstatus(logHis.getActionstatus());
		marketplaceLog.setMessage(logHis.getMessage());
		marketplaceLog.setMessagesource(logHis.getMessagesource());
		modelService.save(marketplaceLog);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
