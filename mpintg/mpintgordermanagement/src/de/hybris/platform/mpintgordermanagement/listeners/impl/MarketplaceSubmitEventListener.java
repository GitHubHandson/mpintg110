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
package de.hybris.platform.mpintgordermanagement.listeners.impl;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.*;

import de.hybris.platform.core.PK;
import de.hybris.platform.marketplaceintegration.model.TmallOrderModel;
import de.hybris.platform.marketplaceintegration.model.TmallOrderEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;
import de.hybris.platform.mpintgordermanagement.constants.MpintgordermanagementConstants;
import de.hybris.platform.mpintgordermanagement.service.ConfirmShipService;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessService;


public class MarketplaceSubmitEventListener implements AfterSaveListener
{

	//9862 - Tmall Order Entry
	private static final int TMALL_ORDER_DEPLOYMENT_CODE = 9862;
	private static Logger log = Logger.getLogger(MarketplaceSubmitEventListener.class);

	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Autowired
	private ConfirmShipService confirmShipService;


	@Override
	public void afterSave(final Collection<AfterSaveEvent> events)
	{
		events.stream().filter(e -> {
			final int code = e.getPk().getTypeCode();
			log.debug("checking event pk(code): " + code + " (expected: " + TMALL_ORDER_DEPLOYMENT_CODE + ")");
			return (e.getType() == AfterSaveEvent.CREATE) && (code == TMALL_ORDER_DEPLOYMENT_CODE);
		}).forEach(e -> {
			final PK pk = e.getPk();
			log.info(String.format("found event{type:%d, pk:%s, type_code:%s, pk_value:%s}", e.getType(), pk,
					pk.getTypeCodeAsString(), pk.getLongValueAsString()));

			final TmallOrderEntryModel entry = modelService.<TmallOrderEntryModel> get(pk);
			final TmallOrderModel order = (TmallOrderModel) entry.getOrder();
			final String TmallOrderEntriesNo = String.valueOf(order.getEntries().size());

			if (order.getTmallOrderEntriesNo().equals(TmallOrderEntriesNo))
			{
				order.setIsCompleted(true);
				modelService.save(order);
			}
		});

		events.stream().filter(e -> {
			final int code = e.getPk().getTypeCode();
			log.debug("checking event pk(code): " + code + " (expected: " + TMALL_ORDER_DEPLOYMENT_CODE + ")");
			return (code == TMALL_ORDER_DEPLOYMENT_CODE && e.getType() != AfterSaveEvent.REMOVE);
		}).forEach(e -> {
			final PK pk = e.getPk();
			log.info(String.format("found event{type:%d, pk:%s, type_code:%s, pk_value:%s}", e.getType(), pk,
					pk.getTypeCodeAsString(), pk.getLongValueAsString()));

			final TmallOrderEntryModel entry = modelService.<TmallOrderEntryModel> get(pk);
			final TmallOrderModel order = (TmallOrderModel) entry.getOrder();
			final String TmallOrderEntriesNo = String.valueOf(order.getEntries().size());
			BaseStoreModel baseStoreModel = order.getMarketplaceStore().getMarketplaceSeller().getBaseStore();
			String submitOrderProcessCode = baseStoreModel.getSubmitOrderProcessCode();
			if (TmallOrderEntriesNo.equals(order.getTmallOrderEntriesNo())
					&& !isContainProcess(order.getOrderProcess(), submitOrderProcessCode)
					&& entry.getEntryStatus().getCode().equals("WAIT_SELLER_SEND_GOODS"))
			{
				order.setStore(order.getMarketplaceStore().getMarketplaceSeller().getBaseStore());
				modelService.save(order);
				final OrderProcessModel TmallContinuationProcess = businessProcessService.<OrderProcessModel> createProcess(
						submitOrderProcessCode + "-" + order.getCode() + "-" + order.getModifiedtime().getTime(),
						submitOrderProcessCode);
				TmallContinuationProcess.setOrder(order);
				modelService.save(TmallContinuationProcess);
				log.info("Start Order continuation-process: '" + TmallContinuationProcess.getCode() + "'");
				businessProcessService.startProcess(TmallContinuationProcess);
			}

			for (ConsignmentModel consignment : order.getConsignments())
			{
				if (confirmShipService.canPerform(consignment))
				{
					final Set<ConsignmentEntryModel> consignmentEntries = consignment.getConsignmentEntries();
					for (final ConsignmentEntryModel consignmentEntry : consignmentEntries)
					{
						final TmallOrderEntryModel orderEntry = (TmallOrderEntryModel) consignmentEntry.getOrderEntry();
						orderEntry.setWaitMarketPlaceResponse(Boolean.TRUE);
					}
				}
			}
		});

	}


	private boolean isContainProcess(Collection<OrderProcessModel> processCollection, String processPrefix)
	{
		if (processCollection.isEmpty() || null == processCollection)
		{
			return false;
		}

		for (OrderProcessModel orderProcess : processCollection)
		{
			if (orderProcess.getCode().startsWith(processPrefix))
			{
				return true;
			}
		}
		return false;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
